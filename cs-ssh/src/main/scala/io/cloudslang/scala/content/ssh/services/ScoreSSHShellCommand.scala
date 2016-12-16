package io.cloudslang.scala.content.ssh.services

import java.util

import io.cloudslang.content.constants.{OutputNames, ReturnCodes}
import io.cloudslang.content.ssh.entities.{CommandResult, ConnectionDetails, KnownHostsFile}
import io.cloudslang.content.ssh.services.SSHService
import io.cloudslang.content.ssh.services.actions.SSHShellAbstract
import io.cloudslang.content.ssh.services.impl.SSHServiceImpl
import io.cloudslang.content.ssh.utils.{IdentityKeyUtils, ProxyUtils, StringUtils}
import io.cloudslang.scala.content.ssh.entities.SSHShellInputs
import io.cloudslang.scala.content.ssh.utils.Constants

/**
  * Created by victor on 12/15/16.
  */
class ScoreSSHShellCommand extends SSHShellAbstract {
  def execute(sshShellInputs: SSHShellInputs): util.Map[String, String] = {
    val returnResult = new util.HashMap[String, String]
    var service = null
    val providerAdded = addSecurityProvider()
    var sessionId = ""
    try {
      sessionId = "sshSession:" + sshShellInputs.host + "-" + sshShellInputs.port + "-" + sshShellInputs.username
      // configure ssh parameters
      val connection = new ConnectionDetails(sshShellInputs.host, sshShellInputs.port,
                                             sshShellInputs.username, sshShellInputs.password)
      val identityKey = IdentityKeyUtils.getIdentityKey(sshShellInputs.privateKeyFile,
                                                        sshShellInputs.privateKeyData,
                                                        sshShellInputs.password)
      val knownHostsFile = new KnownHostsFile(sshShellInputs.knownHostsPath, sshShellInputs.knownHostsPolicy)
      // get the cached SSH session
      service = getSshServiceFromCache(sshShellInputs, sessionId)
      var saveSSHSession = false
      if (service == null || !service.isConnected) {
        saveSSHSession = true
        val proxyHTTP = ProxyUtils.getHTTPProxy(sshShellInputs.proxyHost, sshShellInputs.proxyPort,
                                                sshShellInputs.proxyUsername,
                                                sshShellInputs.proxyPassword)
        service = new SSHServiceImpl(connection, identityKey, knownHostsFile, sshShellInputs.connectTimeout,
                                     sshShellInputs.allowExpectCommands, proxyHTTP, sshShellInputs.allowedCiphers)
      }
      runSSHCommand(sshShellInputs, returnResult, service, sessionId, saveSSHSession)

    } catch {
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
    val timeoutNumber = StringUtils.toInt(sshShellInputs.timeout, Constants.DEFAULT_TIMEOUT)
    val usePseudoTerminal = StringUtils.toBoolean(sshShellInputs.pty, Constants.DEFAULT_USE_PSEUDO_TERMINAL)
    val agentForwarding = StringUtils.toBoolean(sshShellInputs.agentForwarding,
                                                Constants.DEFAULT_USE_AGENT_FORWARDING)
    // run the SSH command
    val commandResult = service.runShellCommand(sshShellInputs.command,
                                                sshShellInputs.characterSet, usePseudoTerminal,
                                                sshShellInputs.connectTimeout, timeoutNumber,
                                                agentForwarding)
    handleSessionClosure(sshShellInputs, service, sessionId, saveSSHSession)
    // populate the results
    populateResult(returnResult, commandResult)
  }

  private def handleSessionClosure(sshShellInputs: SSHShellInputs, service: SSHService, sessionId: String, saveSSHSession: Boolean) {
    val closeSessionBoolean = StringUtils.toBoolean(sshShellInputs.closeSession,
                                                    Constants.DEFAULT_CLOSE_SESSION)
    if (closeSessionBoolean) cleanupService(sshShellInputs, service, sessionId)
    else if (saveSSHSession) {
      // save SSH session in the cache
      val saved = saveToCache(sshShellInputs.sshGlobalSessionObject, service, sessionId)
      if (!saved) throw new RuntimeException("The SSH session could not be saved in the given sessionParam.")
    }
  }

  protected def cleanupService(sshShellInputs: SSHShellInputs, service: SSHService, sessionId: String) {
    service.close()
    service.removeFromCache(sshShellInputs.sshGlobalSessionObject, sessionId)
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
