# Arrowhead_Transparency

To run, simply run the eclipse project. No arguments or configuration required (at this stage)


Using HttpRequester on Firefox:
Usage example:

POST http://[fdfd:55::80ff]:8000/translator
Content-Type: application/xml
```xml
<translatorSetup>
<providerName>http</providerName>
<providerType>http</providerType>
<providerAddress>http://127.0.0.1:7000</providerAddress>
<consumerName>coap</consumerName>
<consumerType>coap</consumerType>
<consumerAddress>127.0.0.1</consumerAddress>
</translatorSetup>
```
 -- response --
200 OK
Content-Type:  text/html
Content-Length:  93
Server:  Jetty(9.1.0.M0)
```xml
<translationendpoint><id>14076</id><ip>127.0.0.1</ip><port>64736</port></translationendpoint>
```


