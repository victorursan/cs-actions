package io.cloudslang.scala.content.ssh.services

import io.cloudslang.content.ssh.services.actions.SSHShellAbstract
import io.cloudslang.scala.content.ssh.entities.SSHShellInputs
import java.util

import io.cloudslang.content.ssh.utils.StringUtils
import io.cloudslang.content.utils.StringUtilities
import io.cloudslang.scala.content.ssh.utils.Constants
/**
  * Created by victor on 12/15/16.
  */
class ScoreSSHShellCommand extends SSHShellAbstract {
  def execute(sshShellInputs: SSHShellInputs): util.Map[String, String] = {
    val returnResult = new util.HashMap[String, String]
    var service = null
    val providerAdded = addSecurityProvider
    var sessionId = ""
    try
      if (sshShellInputs.command.isEmpty) throw new RuntimeException(SSHShellAbstract.COMMAND_IS_NOT_SPECIFIED_MESSAGE)
      if (sshShellInputs.arguments.nonEmpty) sshShellInputs.setCommand(sshShellInputs.getCommand + " " + sshShellInputs.getArguments)
      val portNumber = StringUtils.toInt(sshShellInputs.getPort, Constants.DEFAULT_PORT)
      val knownHostsPolicy = StringUtils.toNotEmptyString(sshShellInputs.getKnownHostsPolicy,
                                                          Constants.DEFAULT_KNOWN_HOSTS_POLICY)
      val knownHostsPath = StringUtils.toPath(sshShellInputs.getKnownHostsPath,
                                              Constants.DEFAULT_KNOWN_HOSTS_PATH)
      sessionId = "sshSession:" + sshShellInputs.getHost + "-" + portNumber + "-" + sshShellInputs.getUsername
      // configure ssh parameters
      val connection = new ConnectionDetails(sshShellInputs.getHost, portNumber,
                                             sshShellInputs.getUsername, sshShellInputs.getPassword)
      val identityKey = IdentityKeyUtils.getIdentityKey(sshShellInputs.getPrivateKeyFile,
                                                        sshShellInputs.getPrivateKeyData,
                                                        sshShellInputs.getPassword)
      val knownHostsFile = new KnownHostsFile(knownHostsPath, knownHostsPolicy)
      // get the cached SSH session
      service = getSshServiceFromCache(sshShellInputs, sessionId)
      var saveSSHSession = false
      if (service == null || !service.isConnected) {
        saveSSHSession = true
        val proxyHTTP = ProxyUtils.getHTTPProxy(sshShellInputs.getProxyHost, sshShellInputs.getProxyPort,
                                                sshShellInputs.getProxyUsername,
                                                sshShellInputs.getProxyPassword)
        service = new SSHServiceImpl(connection, identityKey, knownHostsFile, sshShellInputs.getConnectTimeout,
                                     sshShellInputs.isAllowExpectCommands, proxyHTTP, sshShellInputs.getAllowedCiphers)
      }
      runSSHCommand(sshShellInputs, returnResult, service, sessionId, saveSSHSession)

    catch {
      case e: Exception => {
        if (service != null) cleanupService(sshShellInputs, service, sessionId)
        populateResult(returnResult, e)
      }
    } finally if (providerAdded) removeSecurityProvider()
    returnResult
  }

  private def getSshServiceFromCache(sshShellInputs: SSHShellInputs, sessionId: String) = {
    val service = getFromCache(sshShellInputs, sessionId)
    service
  }

  private def runSSHCommand(sshShellInputs: SSHShellInputs, returnResult: util.Map[String, String], service: SSHService, sessionId: String, saveSSHSession: Boolean) {
    val timeoutNumber = StringUtils.toInt(sshShellInputs.getTimeout, Constants.DEFAULT_TIMEOUT)
    val usePseudoTerminal = StringUtils.toBoolean(sshShellInputs.getPty, Constants.DEFAULT_USE_PSEUDO_TERMINAL)
    val agentForwarding = StringUtils.toBoolean(sshShellInputs.getAgentForwarding,
                                                Constants.DEFAULT_USE_AGENT_FORWARDING)
    sshShellInputs.setCharacterSet(
      StringUtils.toNotEmptyString(sshShellInputs.getCharacterSet, Constants.DEFAULT_CHARACTER_SET))
    // run the SSH command
    val commandResult = service.runShellCommand(sshShellInputs.getCommand,
                                                sshShellInputs.getCharacterSet, usePseudoTerminal,
                                                sshShellInputs.getConnectTimeout, timeoutNumber,
                                                agentForwarding)
    handleSessionClosure(sshShellInputs, service, sessionId, saveSSHSession)
    // populate the results
    populateResult(returnResult, commandResult)
  }

  private def handleSessionClosure(sshShellInputs: SSHShellInputs, service: SSHService, sessionId: String, saveSSHSession: Boolean) {
    val closeSessionBoolean = StringUtils.toBoolean(sshShellInputs.getCloseSession,
                                                    Constants.DEFAULT_CLOSE_SESSION)
    if (closeSessionBoolean) cleanupService(sshShellInputs, service, sessionId)
    else if (saveSSHSession) {
      // save SSH session in the cache
      val saved = saveToCache(sshShellInputs.getSshGlobalSessionObject, service, sessionId)
      if (!saved) throw new RuntimeException("The SSH session could not be saved in the given sessionParam.")
    }
  }

  protected def cleanupService(sshShellInputs: SSHShellInputs, service: SSHService, sessionId: String) {
    service.close()
    service.removeFromCache(sshShellInputs.getSshGlobalSessionObject, sessionId)
  }

  private def populateResult(returnResult: util.Map[String, String], commandResult: CommandResult) {
    returnResult.put(Constants.STDERR, commandResult.getStandardError)
    returnResult.put(Constants.STDOUT, commandResult.getStandardOutput)
    if (commandResult.getExitCode >= 0) {
      returnResult.put(OutputNames.RETURN_RESULT, commandResult.getStandardOutput)
      returnResult.put(OutputNames.RETURN_CODE, ReturnCodes.SUCCESS)
    }
    else {
      returnResult.put(OutputNames.RETURN_RESULT, commandResult.getStandardError)
      returnResult.put(OutputNames.RETURN_CODE, ReturnCodes.FAILURE)
    }
    returnResult.put(Constants.EXIT_STATUS, String.valueOf(commandResult.getExitCode))
  }
}
