package io.cloudslang.scala.content.ssh.utils

import java.nio.file.{Path, Paths}

/**
  * Created by victor on 12/14/16.
  */
object Constants {
  final val EMPTY_STRING = ""
  // inputs
  final val PRIVATE_KEY_DATA = "privateKeyData"
  final val PRIVATE_KEY_FILE = "privateKeyFile"
  final val COMMAND = "command"
  final val ARGS = "arguments"
  final val PTY = "pty"
  final val SSH_SESSIONS_DEFAULT_ID = "sshSessions:default-id"
  final val CLOSE_SESSION = "closeSession"
  final val KNOWN_HOSTS_POLICY = "knownHostsPolicy"
  final val KNOWN_HOSTS_PATH = "knownHostsPath"
  final val ALLOWED_CIPHERS = "allowedCiphers"
  final val ALLOW_EXPECT_COMMANDS = "allowExpectCommands"
  final val PROXY_HOST = "proxyHost"
  final val PROXY_PORT = "proxyPort"
  final val PROXY_USERNAME = "proxyUsername"
  final val PROXY_PASSWORD = "proxyPassword"
  final val CONNECT_TIMEOUT = "connectTimeout"
   // outputs
  final val STDOUT = "STDOUT"
  final val STDERR = "STDERR"
  final val EXIT_STATUS = "exitStatus"
   // default values
  final val DEFAULT_PORT = 22
  final val DEFAULT_PROXY_PORT = 8080
  final val DEFAULT_ALLOW_EXPECT_COMMANDS = false
  final val DEFAULT_TIMEOUT = 90000 //90 seconds
  final val DEFAULT_CONNECT_TIMEOUT = 10000 //10 seconds
  final val DEFAULT_USE_PSEUDO_TERMINAL = false
  final val DEFAULT_USE_AGENT_FORWARDING = false
  final val DEFAULT_NEWLINE = "\\n"
  final val DEFAULT_CHARACTER_SET = "UTF-8"
  final val DEFAULT_CLOSE_SESSION = false
  final val DEFAULT_KNOWN_HOSTS_POLICY = "allow"
  final val DEFAULT_KNOWN_HOSTS_PATH: Path = Paths.get(System.getProperty("user.home"), ".ssh", "known_hosts")
  final val ARGS_IS_DEPRECATED = "This input is deprecated, use the command input to provide arguments."
}

object InputNames {
  final val HOST = "host"
  final val PORT = "port"
  final val USERNAME = "username"
  final val PASSWORD = "password"
  final val CHARACTER_SET = "characterSet"
  final val TIMEOUT = "timeout"
  final val AGENT_FORWARDING = "agentForwarding"
}