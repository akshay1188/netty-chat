/usr/local/bin/protoc --proto_path=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/resources --java_out=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/generated $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/resources/app.proto

/usr/local/bin/protoc --proto_path=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/resources --java_out=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/generated $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/resources/image.proto
