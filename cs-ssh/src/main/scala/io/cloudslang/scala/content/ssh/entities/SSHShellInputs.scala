package io.cloudslang.scala.content.ssh.entities

import java.util

import com.hp.oo.sdk.content.plugin.GlobalSessionObject
import io.cloudslang.content.ssh.entities.SSHConnection

/**
  * Created by victor on 12/15/16.
  */

case class SSHShellInputs(host: String,
                          port: String,
                          username: String,
                          password: String,
                          privateKeyFile: String,
                          command: String,
                          arguments: String,
                          characterSet: String,
                          pty: String,
                          timeout: String,
                          sshGlobalSessionObject: GlobalSessionObject[util.Map[String, SSHConnection]],
                          closeSession: String,
                          knownHostsPolicy: String,
                          knownHostsPath: String,
                          agentForwarding: String,
                          proxyHost: String,
                          proxyPort: String,
                          proxyUsername: String,
                          proxyPassword: String,
                          privateKeyData: String,
                          allowedCiphers: String,
                          allowExpectCommands: Boolean = false,
                          connectTimeout: Int = 0)
