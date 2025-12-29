package fr.stein.maxbooker.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import fr.stein.maxbooker.data.local.maxbookersettings.MaxBookerSettingsProto
import java.io.InputStream
import java.io.OutputStream

object MaxBookerSettingsSerializer : Serializer<MaxBookerSettingsProto> {
    override val defaultValue: MaxBookerSettingsProto =
        MaxBookerSettingsProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): MaxBookerSettingsProto {
        try {
            return MaxBookerSettingsProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: MaxBookerSettingsProto, output: OutputStream) = t.writeTo(output)
}
