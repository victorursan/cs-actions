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
import io.cloudslang.content.ssh.entities
import io.cloudslang.content.utils.{BooleanUtilities, NumberUtilities};

/**
  * Created by victor on 12/15/16.
  */
class ScoreSSHShellCommand extends SSHShellAbstract {
  def execute(sshShellInputs: SSHShellInputs): util.Map[String, String] = {
    val returnResult = new util.HashMap[String, String]
    var service: SSHService = null
    val providerAdded = addSecurityProvider()
    val sessionId = s"sshSession:${sshShellInputs.host}-${sshShellInputs.port}-${sshShellInputs.username}"


    try {
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
        val proxyHTTP = ProxyUtils.getHTTPProxy(sshShellInputs.proxyHost, sshShellInputs.proxyPort, sshShellInputs.proxyUsername, sshShellInputs.proxyPassword)
        service = new SSHServiceImpl(connection, identityKey, knownHostsFile, sshShellInputs.connectTimeout, sshShellInputs.allowExpectCommands, proxyHTTP, sshShellInputs.allowedCiphers)
      }
      runSSHCommand(sshShellInputs, returnResult, service, sessionId, saveSSHSession)

    } catch {
      case e: Exception =>
        if (service != null) {
          cleanupService(sshShellInputs, service, sessionId)
        }
        populateResult(returnResult, e)
    } finally {
      if (providerAdded) removeSecurityProvider()
    }
    returnResult
  }

  private def getSshServiceFromCache(sshShellInputs: SSHShellInputs, sessionId: String): SSHService = {
    val service = getFromCache(convert(sshShellInputs), sessionId)
    service
  }

  private def runSSHCommand(sshShellInputs: SSHShellInputs, returnResult: util.Map[String, String], service: SSHService, sessionId: String, saveSSHSession: Boolean) {
    val timeoutNumber = NumberUtilities.toInteger(sshShellInputs.timeout, Constants.DEFAULT_TIMEOUT)
    val usePseudoTerminal = BooleanUtilities.toBoolean(sshShellInputs.pty, Constants.DEFAULT_USE_PSEUDO_TERMINAL)
    val agentForwarding = BooleanUtilities.toBoolean(sshShellInputs.agentForwarding, Constants.DEFAULT_USE_AGENT_FORWARDING)
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
    val closeSessionBoolean = StringUtils.toBoolean(sshShellInputs.closeSession, Constants.DEFAULT_CLOSE_SESSION)
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

  def convert(sSHShellInputs: SSHShellInputs): entities.SSHShellInputs = {
    val javaSSHShellInputs = new entities.SSHShellInputs()
    javaSSHShellInputs.setHost(sSHShellInputs.host)
    javaSSHShellInputs.setPort(sSHShellInputs.port.toString)
    javaSSHShellInputs.setUsername(sSHShellInputs.username)
    javaSSHShellInputs.setPassword(sSHShellInputs.password)
    javaSSHShellInputs.setPrivateKeyFile(sSHShellInputs.privateKeyFile)
    javaSSHShellInputs.setPrivateKeyData(sSHShellInputs.privateKeyData)
    javaSSHShellInputs.setCommand(sSHShellInputs.command)
    javaSSHShellInputs.setArguments(sSHShellInputs.arguments)
    javaSSHShellInputs.setCharacterSet(sSHShellInputs.characterSet)
    javaSSHShellInputs.setPty(sSHShellInputs.pty)
    javaSSHShellInputs.setAgentForwarding(sSHShellInputs.agentForwarding)
    javaSSHShellInputs.setTimeout(sSHShellInputs.timeout)
    javaSSHShellInputs.setConnectTimeout(sSHShellInputs.connectTimeout.toString)
    javaSSHShellInputs.setSshGlobalSessionObject(sSHShellInputs.sshGlobalSessionObject)
    javaSSHShellInputs.setCloseSession(sSHShellInputs.closeSession)
    javaSSHShellInputs.setKnownHostsPolicy(sSHShellInputs.knownHostsPolicy)
    javaSSHShellInputs.setKnownHostsPath(sSHShellInputs.knownHostsPath.toString)
    javaSSHShellInputs.setAllowedCiphers(sSHShellInputs.allowedCiphers)
    javaSSHShellInputs.setProxyHost(sSHShellInputs.proxyHost)
    javaSSHShellInputs.setProxyPort(sSHShellInputs.proxyPort)
    javaSSHShellInputs.setProxyUsername(sSHShellInputs.proxyUsername)
    javaSSHShellInputs.setProxyPassword(sSHShellInputs.proxyPassword)
    javaSSHShellInputs.setAllowExpectCommands(sSHShellInputs.allowExpectCommands.toString)
    javaSSHShellInputs
  }
}
