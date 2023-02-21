# BofA Demo

18 Concurrent Sessions   
4 Real Device Sessions

Runs test on Bank of America Site.

Cross Browser, Android, and iOS Real Devices

```shell
export SAUCE_REGION="us-west-1"
mvn dependency:resolve && mvn test-compile
mvn test
```



### Run via Saucectl in [Hosted Orchestration (Beta)](https://docs.saucelabs.com/hosted-orchestration/)

```shell
export SAUCE_REGION="us-west-1"
saucectl run
```