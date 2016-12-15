package io.cloudslang.scala.content.ssh.actions

import java.util

import com.hp.oo.sdk.content.annotations.{Action, Output, Param, Response}
import com.hp.oo.sdk.content.plugin.ActionMetadata.{MatchType, ResponseType}
import com.hp.oo.sdk.content.plugin.GlobalSessionObject
import io.cloudslang.content.constants.{OutputNames, ResponseNames, ReturnCodes}
import io.cloudslang.content.ssh.entities.SSHConnection
import io.cloudslang.content.utils.{BooleanUtilities, NumberUtilities, StringUtilities}
import io.cloudslang.scala.content.ssh.entities.SSHShellInputs
import io.cloudslang.scala.content.ssh.utils.{Constants, InputNames}

/**
  * Created by victor on 12/13/16.
  */
class SSHShellCommandAction2 {

  /**
    * Executes a Shell command(s) on the remote machine using the SSH protocol.
    *
    * @param host                The hostname or the ip address of the remote machine.
    * @param port                The port number for running the command. It overwrites the port given inside the host input (in a syntax like host:port), if this exists.
    * @param username            The username of the account on the remote machine.
    * @param password            The password of the user. If using a private key file this will be used as the passphrase for the file.
    * @param privateKeyFile      The path to the private key file (OpenSSH type) on the machine where is the worker.
    * @param privateKeyData      A string representing the private key (OpenSSH type) used for authenticating the user.
    *                            This string is usually the content of a private key file. The 'privateKeyData' and the
    *                            'privateKeyFile' inputs are mutually exclusive. For security reasons it is recommend
    *                            that the private key be protected by a passphrase that should be provided through the
    *                            'password' input.
    * @param knownHostsPolicy    The policy used for managing known_hosts file. Valid values: allow, strict, add. Default value: allow
    * @param knownHostsPath      The path to the known hosts file.
    * @param command             The command(s) to execute.
    * @param arguments           The arguments to pass to the command.
    * @param characterSet        The character encoding used for input stream encoding from the target machine.
    *                            Valid values: SJIS, EUC-JP, UTF-8. Default value: UTF-8.
    * @param pty                 Whether to use a pseudo-terminal (PTY) session. Valid values: false, true. Default value: false
    * @param agentForwarding     Enables or disables the forwarding of the authentication agent connection.
    *                            Agent forwarding should be enabled with caution.
    * @param timeout             Time in milliseconds to wait for the command to complete. Default value is 90000 (90 seconds)
    * @param connectTimeout      Time in milliseconds to wait for the connection to be made. Default value: 10000
    * @param allowedCiphers      A comma separated list of ciphers that will be used in the client-server handshake
    *                            mechanism when the connection is created. Check the notes section for security concerns
    *                            regarding your choice of ciphers. The default value will be used even if the input is not
    *                            added to the operation.
    *                            Default value: aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc
    * @param globalSessionObject the sessionObject that holds the connection if the close session is false.
    * @param closeSession        If true it closes the SSH session at completion of this operation.
    *                            If false the SSH session will be cached for future calls of this operation during the life of the flow.
    *                            Valid values: false, true. Default value: false
    * @return - a map containing the output of the operation. Keys present in the map are:
    *         <br><b>returnResult</b> - The primary output.
    *         <br><b>STDOUT</b> - The standard output of the command(s).
    *         <br><b>visualized</b> - The output of the command in XML format.
    *         <br><b>returnCode</b> - the return code of the operation. 0 if the operation goes to success, -1 if the operation goes to failure.
    *         <br><b>exception</b> - the exception message if the operation goes to failure.
    */
  @Action(name = "SSH Command Scala",
          outputs = Array(
            new Output(OutputNames.RETURN_CODE),
            new Output(OutputNames.RETURN_RESULT),
            new Output(OutputNames.EXCEPTION),
            new Output(Constants.STDOUT),
            new Output(Constants.STDERR),
            new Output(Constants.EXIT_STATUS)),
          responses = Array(
            new Response(text = ResponseNames.SUCCESS, field = OutputNames.RETURN_CODE, value = ReturnCodes.SUCCESS,
                         matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.RESOLVED),
            new Response(text = ResponseNames.FAILURE, field = OutputNames.RETURN_CODE, value = ReturnCodes.FAILURE,
                         matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.ERROR, isOnFail = true)
          ))
  def execute(@Param(value = InputNames.HOST, required = true) host: String,
              @Param(InputNames.PORT) port: String,
              @Param(value = InputNames.USERNAME, required = true) username: String,
              @Param(value = InputNames.PASSWORD, encrypted = true) password: String,
              @Param(Constants.PRIVATE_KEY_FILE) privateKeyFile: String,
              @Param(value = Constants.PRIVATE_KEY_DATA, encrypted = true) privateKeyData: String,
              @Param(Constants.KNOWN_HOSTS_POLICY) knownHostsPolicy: String,
              @Param(Constants.KNOWN_HOSTS_PATH) knownHostsPath: String,
              @Param(Constants.ALLOWED_CIPHERS) allowedCiphers: String,
              @Param(value = Constants.COMMAND, required = true) command: String,
              @Param(value = Constants.ARGS, description = Constants.ARGS_IS_DEPRECATED) arguments: String,
              @Param(InputNames.CHARACTER_SET) characterSet: String,
              @Param(value = Constants.PTY) pty: String,
              @Param(value = InputNames.AGENT_FORWARDING) agentForwarding: String,
              @Param(InputNames.TIMEOUT) timeout: String,
              @Param(Constants.CONNECT_TIMEOUT) connectTimeout: String,
              @Param(
                Constants.SSH_SESSIONS_DEFAULT_ID) globalSessionObject: GlobalSessionObject[util.Map[String, SSHConnection]],
              @Param(Constants.CLOSE_SESSION) closeSession: String,
              @Param(Constants.PROXY_HOST) proxyHost: String,
              @Param(Constants.PROXY_PORT) proxyPort: String,
              @Param(Constants.PROXY_USERNAME) proxyUsername: String,
              @Param(value = Constants.PROXY_PASSWORD, encrypted = true) proxyPassword: String,
              @Param(Constants.ALLOW_EXPECT_COMMANDS) allowExpectCommands: String): util.Map[String, String] = {

    val inputs = SSHShellInputs(host = host,
                                port = port,
                                username = username,
                                password = password,
                                privateKeyFile = privateKeyFile,
                                command = command,
                                arguments = arguments,
                                characterSet = characterSet,
                                pty = pty,
                                timeout = timeout,
                                sshGlobalSessionObject = globalSessionObject,
                                closeSession = closeSession,
                                knownHostsPolicy = knownHostsPolicy,
                                knownHostsPath = knownHostsPath,
                                agentForwarding = agentForwarding,
                                proxyHost = proxyHost,
                                proxyPort = proxyPort,
                                proxyUsername = proxyUsername,
                                proxyPassword = proxyPassword,
                                privateKeyData = privateKeyData,
                                allowedCiphers = allowedCiphers,
                                allowExpectCommands = BooleanUtilities.toBoolean(allowExpectCommands, Constants.DEFAULT_ALLOW_EXPECT_COMMANDS),
                                connectTimeout = NumberUtilities.toInteger(connectTimeout, 0))
    new util.HashMap[String, String]()
  }


}
