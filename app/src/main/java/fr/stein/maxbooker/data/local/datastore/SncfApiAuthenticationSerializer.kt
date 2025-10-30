package fr.stein.maxbooker.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import java.io.InputStream
import java.io.OutputStream

object SncfApiAuthenticationSerializer : Serializer<SncfApiAuthenticationProto> {
    override val defaultValue: SncfApiAuthenticationProto =
        SncfApiAuthenticationProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SncfApiAuthenticationProto {
        try {
            return SncfApiAuthenticationProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SncfApiAuthenticationProto, output: OutputStream) =
        t.writeTo(output)
}
