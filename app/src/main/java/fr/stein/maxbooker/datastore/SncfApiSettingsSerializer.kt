package fr.stein.maxbooker.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import fr.stein.maxbooker.proto.SncfApiSettings
import java.io.InputStream
import java.io.OutputStream

class SncfApiSettingsSerializer: Serializer<SncfApiSettings> {
    override val defaultValue: SncfApiSettings = SncfApiSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SncfApiSettings {
        try {
            return SncfApiSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto !", exception)
        }
    }

    override suspend fun writeTo(t: SncfApiSettings, output: OutputStream) {
        t.writeTo(output)
    }
}