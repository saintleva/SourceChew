package com.github.saintleva.sourcechew.secure


import com.russhwolf.settings.Settings

class WebSecureStorage(
    private val settings: Settings,
private val settings: Settings
) : SecureKeyValueStorage {

    /**
     * Записывает строковое значение в localStorage по указанному ключу.
     * Существующее значение будет перезаписано.
     *
     * @param keyName Ключ, под которым будет сохранено значение.
     * @param value Строка для сохранения.
     */
    override suspend fun write(keyName: String, value: String) {
        settings.putString(keyName, value)
    }

    /**
     * Читает строковое значение из localStorage по указанному ключу.
     *
     * @param keyName Ключ для чтения.
     * @return Сохраненное значение или `null`, если по этому ключу ничего нет.
     */
    override suspend fun read(keyName: String): String? {
        return settings.getStringOrNull(keyName)
    }

    /**
     * Удаляет значение из localStorage по указанному ключу.
     *
     * @param keyName Ключ значения, которое нужно удалить.
     */
    override suspend fun remove(keyName: String) {
        settings.remove(keyName)
    }
}