package io.cloudslang.scala.content.ssh.utils

import java.nio.file.{Path, Paths}

/**
  * Created by victor on 12/14/16.
  */
object Constants {
  val EMPTY_STRING = ""
  // inputs
  val PRIVATE_KEY_DATA = "privateKeyData"
  val PRIVATE_KEY_FILE = "privateKeyFile"
  val COMMAND = "command"
  val ARGS = "arguments"
  val PTY = "pty"
  val SSH_SESSIONS_DEFAULT_ID = "sshSessions:default-id"
  val CLOSE_SESSION = "closeSession"
  val KNOWN_HOSTS_POLICY = "knownHostsPolicy"
  val KNOWN_HOSTS_PATH = "knownHostsPath"
  val ALLOWED_CIPHERS = "allowedCiphers"
  val ALLOW_EXPECT_COMMANDS = "allowExpectCommands"
  val PROXY_HOST = "proxyHost"
  val PROXY_PORT = "proxyPort"
  val PROXY_USERNAME = "proxyUsername"
  val PROXY_PASSWORD = "proxyPassword"
  val CONNECT_TIMEOUT = "connectTimeout"
  // outputs
  val STDOUT = "STDOUT"
  val STDERR = "STDERR"
  val EXIT_STATUS = "exitStatus"
  // default values
  val DEFAULT_PORT = 22
  val DEFAULT_PROXY_PORT = 8080
  val DEFAULT_ALLOW_EXPECT_COMMANDS = false
  val DEFAULT_TIMEOUT = 90000 //90 seconds
  val DEFAULT_CONNECT_TIMEOUT = 10000 //10 seconds
  val DEFAULT_USE_PSEUDO_TERMINAL = false
  val DEFAULT_USE_AGENT_FORWARDING = false
  val DEFAULT_NEWLINE = "\\n"
  val DEFAULT_CHARACTER_SET = "UTF-8"
  val DEFAULT_CLOSE_SESSION = false
  val DEFAULT_KNOWN_HOSTS_POLICY = "allow"
  val DEFAULT_KNOWN_HOSTS_PATH: Path = Paths.get(System.getProperty("user.home"), ".ssh", "known_hosts")
  val ARGS_IS_DEPRECATED = "This input is deprecated, use the command input to provide arguments."
}

object InputNames {
  val HOST = "host"
  val PORT = "port"
  val USERNAME = "username"
  val PASSWORD = "password"
  val CHARACTER_SET = "characterSet"
  val TIMEOUT = "timeout"
  val AGENT_FORWARDING = "agentForwarding"
}