package fr.stein.maxbooker.data.local.sncfcustomer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SncfCustomerSerializer : Serializer<SncfCustomerProto> {
    override val defaultValue: SncfCustomerProto =
        SncfCustomerProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SncfCustomerProto {
        try {
            return SncfCustomerProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SncfCustomerProto, output: OutputStream) = t.writeTo(output)
}
