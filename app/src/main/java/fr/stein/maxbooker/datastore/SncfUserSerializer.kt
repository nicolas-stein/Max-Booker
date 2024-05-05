package fr.stein.maxbooker.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import fr.stein.maxbooker.proto.SncfUser
import java.io.InputStream
import java.io.OutputStream

class SncfUserSerializer: Serializer<SncfUser> {
    override val defaultValue: SncfUser = SncfUser.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SncfUser {
        try {
            return SncfUser.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto !", exception)
        }
    }

    override suspend fun writeTo(t: SncfUser, output: OutputStream) {
        t.writeTo(output)
    }
}