## Kintu - Welcome

Kintu stands for Kafka Integrated Test Utility

Currently only JSON payloads are supported.

Kintu enables developers to save kafka test events in their project repo, emit those events on demand, and randomize specified fields to bypass idempotency checks

### Installation

Automatic installation is currently via [Homebrew](brew.sh)

> brew install admdecker/admdecker/kintu

### Commands

- kintu init
  - Initializes your repo with a kintu.conf file. 
  - This is where your Kafka config should go.
  - Kintu will climb up the folder structure to find the nearest kafka config, so only create one per file tree
- kintu new \<name>
  - Creates a new kintu file for defining a kafka payload
- kintu emit \<name>
  - Emits an existing kafka payload from a .kintu file

### Kintu File

- The kintu file is a JSON file containing some metadata along with the message payload

It looks like this:

    { 
        "topic": "test-topic"
        "randomize": []
        "payload": {
            "sample": "payload"
        }
    }

- topic: The Kafka topic to send the message to
- randomize: an array of JSON paths to randomize.  Select the properties you want to randomize using [Jayway JSON Path syntax](https://github.com/json-path/JsonPath)
- payload: The payload that will be sent to the Kafka topic


### Feature Wishlist

- Multiple environment selection
- Other event broker support such as Kinesis or Azure Messaging Services
- Support for non-JSON payloads
