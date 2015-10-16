# Sproutigy Java Commons Binary
JVM library that provides consistent abstraction level over data from byte arrays, byte buffers, streams and files as also different string encoding types.
It allows adaptation or conversion from one type to another. It's easier and less error-prone to maintain single data source object instead of multiple different low-level types.
This library provides `Binary` abstract class along with multiple implementors (while still you can provide your own), as also `BinaryBuilder` and `BinaryMap` interfaces with default implementation.


## Requirements and dependencies
Requires Java 6 or later. No additional dependencies.


## Elements


### Binary
`Binary` is an abstraction over bytes-based data source, such as byte array, string (with known encoding), streams and even files. `Binary` has data-based implementation of `equals()`, `compareTo()` and `hashCode()` so it can be safely used to do comparisons or as keys in maps. `Binary` is immutable (read-only).
`Binary.EMPTY` static instance is always available to represent empty data.

Low-level type | Input - wrapping static method | Output - wrapper converter method 
--- | --- | ---
Byte Array | `Binary.fromByteArray(bytes)` | `byte[] asByteArray()`
Byte Buffer | `Binary.fromByteBuffer(byteBuffer)` | `ByteBuffer asByteBuffer()`
String | `Binary.fromString(s, charset)` | `String asString(charset)`
String ASCII | `Binary.fromStringASCII(s)` | `String asStringASCII()`
String UTF-8 | `Binary.fromStringUTF8(s)` | `String asStringUTF8()`
String UTF-16 | `Binary.fromStringUTF16(s)` | `String asStringUTF16()`
String UTF-32 | `Binary.fromStringUTF32(s)` | `String asStringUTF32()`
Stream | `Binary.fromStream(inputStream)` | `InputStream asStream()` or `void toStream(outputStream)`
File | `Binary.fromFile(fileOrPath)` | `void toFile(fileOrPath)` or `String toTempFile()`
Hex String | `Binary.fromHex(s)` | `String asHex()`
Base64 String | `Binary.fromBase64(s)` | `String asBase64()`

Plus some additional methods:
- `subrange(offset, length)` returns subrange of current Binary
- `hasLength()` returns `true` when length is available or `false` when it is required to read whole data source to count bytes
- `length()` returns length of data in bytes 

#### Examples

##### Read file into a string
```java
String content = Binary.fromFile(file).asStringUTF8();
```

##### Make input stream from a string
```java
InputStream stream = Binary.fromStringASCII("HELLO").asStream();
```

##### Write string to an output stream
```java
Binary.fromStringASCII("HELLO").toStream(outputStream);
```

##### Count length of a stream
```java
long len = Binary.fromStream(inputStream).length();
```

##### Use subrange of data
```java
Binary.fromFile(file).subrange(0,5).asStringUTF8();
```


##### Empty data
```java
byte[] emptyByteArray = Binary.EMPTY.asByteArray();
```


### BinaryBuilder
`BinaryBuilder` allows to append any type of low-level data to finally build `Binary`.
When data is rather small it is kept in memory. To prevent OutOfMemoryException, when it reaches predefined limits, its content is written to temporary file and all further append requests are targeting there.
`BinaryBuilder` implements `OutputStream`, so can be used as a target stream.

#### Example
```java
Binary myData = new BinaryBuilder().fromStringASCII("HELL").append( (byte)79 ).build();
```

### BinaryMap
`BinaryMap` is just an interface that extends `Map<Binary, Binary>` which means that any `Binary` may be used both as a key and a value.
`DefaultBinaryMap` is default implementation of `BinaryMap`. It is based on `LinkedHashMap` and therefore is not thread-safe.


#### Example
```java
BinaryMap map = new DefaultBinaryMap();
map.put(Binary.fromStringASCII("Hello"), Binary.fromByte((byte)65));
map.put(Binary.fromByte((byte) 90), Binary.fromStringUTF8("World"));
```


## Maven

To use as a dependency add to your `pom.xml` into `<dependencies>` section: 
```xml
<dependency>
    <groupId>com.sproutigy.commons</groupId>
    <artifactId>binary</artifactId>
    <version>RELEASE</version>
</dependency>
```

## More
For more information and commercial support visit [Sproutigy](http://www.sproutigy.com/opensource)
