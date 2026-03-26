# How to Run — Certificate Feed Generator

## Prerequisites

- Java 11+
- Maven 3.6+
- Docker (optional, for containerized run)

---

## 1. Build the project

```bash
mvn clean package -DskipTests
```

This creates the JAR at `target/java-case-study-1.0.0.jar`.

To build **with tests** (831 unit tests):

```bash
mvn clean package
```

---

## 2. Run from command line

```bash
java -cp target/classes com.solvians.showcase.App <threads> <quotes>
```

| Argument | Description |
|----------|-------------|
| `threads` | Number of threads in the pool (e.g., 10) |
| `quotes` | Total number of certificate updates to generate (e.g., 50) |

### Examples

**10 threads, 50 certificates:**
```bash
java -cp target/classes com.solvians.showcase.App 10 50
```

**1 thread, 5 certificates (minimal):**
```bash
java -cp target/classes com.solvians.showcase.App 1 5
```

**50 threads, 10000 certificates (stress test):**
```bash
java -cp target/classes com.solvians.showcase.App 50 10000
```

**Using the JAR directly:**
```bash
java -jar target/java-case-study-1.0.0.jar 10 50
```

---

## 3. Run with Maven exec plugin

```bash
mvn compile exec:java -Dexec.mainClass="com.solvians.showcase.App" -Dexec.args="10 50"
```

---

## 4. Run tests only

```bash
mvn test
```

---

## 5. Run with Docker

**Build the image:**
```bash
docker build -t cert-feed-generator .
```

**Run with default args (10 threads, 50 quotes):**
```bash
docker run --rm cert-feed-generator
```

**Run with custom args:**
```bash
docker run --rm cert-feed-generator 50 10000
```

**Run with Docker Compose (default args):**
```bash
docker-compose up
```

**Run with Docker Compose (custom args):**
```bash
THREADS=20 QUOTES=500 docker-compose up
```

---

## Output format

Each line is a comma-separated certificate update:

```
timestamp,ISIN,bidPrice,bidSize,askPrice,askSize
```

Example:
```
1352122280502,DE1234567896,101.23,1000,103.45,1000
```

| Field | Description |
|-------|-------------|
| timestamp | Epoch milliseconds |
| ISIN | 12-char security ID (2 letters + 9 alphanumeric + 1 check digit) |
| bidPrice | 100.00–200.00 (2 decimal places) |
| bidSize | 1000–5000 (integer) |
| askPrice | 100.00–200.00 (2 decimal places) |
| askSize | 1000–10000 (integer) |

---

## Sample output

```
================================================================================
================================================================================
  CERTIFICATE FEED GENERATOR
  Threads: 10 | Quotes: 50 | Available Processors: 8
================================================================================
1774552889520,LQ6TU16ZAFX8,120.00,3992,167.38,9817
1774552889520,MQDG87IOSPV0,112.33,1672,125.33,1618
1774552889520,UITNH77VCCA6,169.48,4688,197.33,9200
1774552889520,DORI0X0O1UR7,180.54,3096,161.13,4994
1774552889520,IGFU2HFKDOH2,180.35,3628,148.01,4793
1774552889521,ZX540FZGEAX6,181.62,2463,186.12,9502
1774552889520,GR0A1OVO5JI7,154.25,4170,135.49,6020
1774552889521,ZD8TZL3JGMI8,144.17,2227,117.76,9164
1774552889521,CBWGD777MQK2,139.53,3631,137.85,4279
1774552889521,COJFTP15ME58,193.83,4221,142.00,1139
1774552889521,MEEM71PACWL1,124.30,3725,146.01,1513
1774552889521,PPWQEKIKKZQ0,112.48,3238,102.89,5639
1774552889521,JIK8OS357IO8,137.63,1517,128.56,1670
1774552889521,PXN9JM6ZMID6,124.75,3664,176.57,3798
1774552889522,TL2D0Z29RYC0,108.63,1817,176.22,3890
1774552889522,VUVMESJPGH90,162.77,3175,168.57,8572
1774552889522,ST3G04P13UF2,120.85,2424,144.38,1284
1774552889522,AN622OFP5LS1,192.56,4367,100.72,2937
1774552889522,VH57YZV921L1,169.78,4700,130.13,7905
1774552889522,UG3YKZPW7W40,161.75,2149,116.19,3863
1774552889522,XNH8U9E9EE12,135.34,3109,166.96,6020
1774552889522,JPH8Z26D0G26,137.56,1405,114.75,5045
1774552889523,DVWDT1HLQU36,189.06,1758,108.90,1337
1774552889523,TM47KVQQBNV1,147.70,2048,117.64,8574
1774552889523,AST8RCOK5XP1,106.15,1683,115.55,2458
1774552889523,MSL10ZG75CI1,166.77,1740,167.52,9383
1774552889523,VA01H5JPU905,181.95,2590,164.88,5278
1774552889523,QVAJ9ZUX34M8,132.44,3918,187.40,3001
1774552889523,MAZS9XN7PYF4,102.51,1450,137.78,5558
1774552889523,JTCTVW2EOUT7,162.87,1299,177.31,4038
1774552889523,KXT801LGBXL1,129.91,3443,122.75,7874
1774552889524,WCEHPD6YJAZ0,105.96,2926,121.52,5900
1774552889524,EYOEN5PH58O1,151.60,2340,198.37,7865
1774552889524,VRZ5XPEVR2C8,197.76,4063,132.23,3361
1774552889524,GYNBTBUOWHC9,134.13,4014,137.72,9962
1774552889524,JB554GULIB62,182.09,2380,154.30,4542
1774552889524,ZFGMFK7R6E37,102.06,1840,161.68,5795
1774552889524,BEX2NT645SK8,153.74,3506,123.74,8907
1774552889524,BEM2JBO97QQ4,141.38,1002,158.40,2755
1774552889524,PTYKCASWSN74,107.25,4960,104.94,7682
1774552889524,MJTIX05RSNK9,118.93,2840,148.27,9720
1774552889525,VVP50O5EUVM4,188.13,4965,153.32,3202
1774552889525,VM9S10H1IX85,100.70,2525,187.40,3404
1774552889525,CCSWNEV096H4,118.81,4059,169.87,6275
1774552889526,WV2DKGYF19B2,126.65,4901,177.82,5848
1774552889528,ZV0MEY99TH09,150.16,4081,192.39,2983
1774552889528,PBJ994CJ6A99,152.17,1813,138.16,1485
1774552889528,KRGA15L6Y2T9,192.94,3609,191.51,8947
1774552889528,CK4CQ6B1GGM9,157.78,3000,132.74,8812
1774552889528,OPFCB4Y69FJ3,179.96,4158,152.82,8381
================================================================================
  PERFORMANCE METRICS
  Total generated : 50 certificate updates
  Threads used    : 10
  Execution time  : 16 ms
  Throughput      : 3125 quotes/sec
```
