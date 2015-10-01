# Sproutigy Java Commons RawData
JVM library that provides consistent abstraction level over data from byte arrays, byte buffers, streams and files.
It allows adaptation or conversion from one type to another and maintain single data source layer instead of different low-level types.
This library provides `RawData` abstract class along with multiple implementors (but you can provide your own), as also `RawDataBuilder` and `RawDataMap` interface with default implementation.


## Requirements and dependencies
Requires Java 6 or later.
This library has following dependency:
- [SLF4J](http://www.slf4j.org/)

## Elements


### RawData
`RawData` is an abstraction over bytes-based data source, such as byte array, string (with known encoding), streams and even files. `RawData` has data-based implementation of `equals()`, `compareTo()` and `hashCode()` so it can be safely used to do comparisons or as keys in maps. `RawData` is immutable (read-only).
`RawData.EMPTY` static instance is always available to represent empty data.

Low-level type | Input - wrapping static method | Output - wrapper converter method 
--- | --- | ---
Byte Array | `RawData.fromByteArray(bytes)` | `byte[] asByteArray()`
Byte Buffer | `RawData.fromByteBuffer(byteBuffer)` | `ByteBuffer asByteBuffer()`
String | `RawData.fromString(s, charset)` | `String asString(charset)`
String ASCII | `RawData.fromStringASCII(s)` | `String asStringASCII()`
String UTF-8 | `RawData.fromStringUTF8(s)` | `String asStringUTF8()`
String UTF-16 | `RawData.fromStringUTF16(s)` | `String asStringUTF16()`
String UTF-32 | `RawData.fromStringUTF32(s)` | `String asStringUTF32()`
Stream | `RawData.fromStream(inputStream)` | `InputStream asStream()` or `void toStream(outputStream)`
File | `RawData.fromFile(fileOrPath)` | `void toFile(fileOrPath)` or `String toTempFile()`

Plus some additional methods:
- `subrange(offset, length)` returns subrange of current RawData
- `hasLength()` returns `true` when length is available or `false` when it is required to read whole data source to count bytes
- `length()` returns length of data in bytes 

#### Examples

##### Read file into a string
```java
String content = RawData.fromFile(file).asStringUTF8();
```

##### Make input stream from a string
```java
InputStream stream = RawData.fromStringASCII("HELLO").asStream();
```

##### Write string to an output stream
```java
RawData.fromStringASCII("HELLO").toStream(outputStream);
```

##### Count length of a stream
```java
long len = RawData.fromStream(inputStream).length();
```

#### Use subrange of data
```java
RawData.fromFile(file).subrange(0,5).asStringUTF8();
```


##### Empty data
```java
byte[] emptyByteArray = RawData.EMPTY.asByteArray();
```


### RawDataBuilder
`RawDataBuilder` allows to append any type of low-level data to finally build `RawData`.
When data is rather small it is kept in memory. To prevent OutOfMemoryException, when it reaches predefined limits, its content is written to temporary file and all further append requests are targeting there.
`RawDataBuilder` implements `OutputStream`, so can be used as a target stream.

#### Example
```java
RawData myData = new RawDataBuilder().fromStringASCII("HELL").append( (byte)79 ).build();
```

### RawDataMap
`RawDataMap` is just an interface that extends `Map<RawData, RawData>` which means that any `RawData` may be used both as a key and a value.
`DefaultRawDataMap` is default implementation of `RawDataMap`. It is based on `LinkedHashMap` and therefore is not thread-safe.


#### Example
```java
RawDataMap map = new DefaultRawDataMap();
map.put(RawData.fromStringASCII("Hello"), RawData.fromByte((byte)65));
map.put(RawData.fromByte((byte) 90), RawData.fromStringUTF8("World"));
```


## Maven

To use as a dependency add to your `pom.xml` into `<dependencies>` section: 
```xml
<dependency>
    <groupId>com.sproutigy.commons</groupId>
    <artifactId>rawdata</artifactId>
    <version>RELEASE</version>
</dependency>
```

## More
For more information and commercial support visit [Sproutigy](http://www.sproutigy.com/opensource)
