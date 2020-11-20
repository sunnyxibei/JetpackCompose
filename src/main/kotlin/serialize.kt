import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

fun Any.serialize(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(this)
    return byteArrayOutputStream.toByteArray()
}

fun <T> ByteArray.deserialize(): T {
    val byteArrayInputStream = ByteArrayInputStream(this)
    val objectInputStream = ObjectInputStream(byteArrayInputStream)
    return objectInputStream.readObject() as T
}