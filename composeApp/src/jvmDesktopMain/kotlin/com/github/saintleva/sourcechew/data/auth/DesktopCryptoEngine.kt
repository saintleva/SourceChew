package com.github.saintleva.sourcechew.data.auth

import javax.crypto.SecretKey


class DesktopCryptoEngine(key: SecretKey) : AesGcmCryptoEngine(key)