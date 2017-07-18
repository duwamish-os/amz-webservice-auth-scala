
install cert for endpoint so that stupid java can TLS connect, 

```bash
git clone https://github.com/escline/InstallCert
javac InstallCert.java
java InstallCert endpoint:443
```

create [base64 b2t hash](https://wiki.openssl.org/index.php/Enc#Base64_Encoding) of username:password

```
openssl enc -base64 <<< 'your_amz_username:your_amz_password'
opensssl enc -base64 -d <<< whatever_bas64
```

| config                |                                       |
|-----------------------|---------------------------------------|
| amz.endpoint          | _                                     |
| amz.resources.roles   | /authentication/roleArns              |
| amz.resources.tokens  | /authentication/awsToken              |
| amz.basic.auth        | hashcode of username and passowrd     |
| amz.username          | prayagupd                             |
| amz.password          | _                                     |
| auth.credentials.path | /Users/prayagupd/.aws/credentials     |
| auth.credentials.name | default                               |

```
sbt run
```

```bash
curl -XPOST --header "Content-Type application/json" --header "Authorization: Basic base64_hash" -d '{"Role":"arn:aws:iam::accountId:role/SomeRole","Principal":"arn:aws:iam::accountId:saml-provider/DWM"}' https://pbcld-awstoken.duwamish.net/authentication/awsToken

{
"SecretAccessKey": "ddEv+m662v/nj/rwT3t3GFAzhMWtYxxM0hJdiWhR", 
"AccessKey": "ASIAJNSKM6F5YRE5XQCA", 
"Expiration": "2017-07-15T06:26:31+00:00", 
"SessionToken": "FQoDYXdzEP///////////wEaDKs8IhI89qWl6HnoqSK1Ah/o3rWygDoX9KkrzrRH7FV4G2QCM1vNuTc6vPvEOME0vPkJjmnGscXwQSTW6VfGEF4wnn7elXttWO7j+YykUrZCmI6CZpgdeUf9eVUz0OazpgEyDjVmYJ70cFXFD+bQM6ezkAOEfV8gFCl8roi0aKTA4OgHoSZre2E8N54sZg5olC4d2wuxhRuZNM/w0rauntgUTMWCFGuO+7j5gsu3vjSYBOURkRVPPN1BJTXGW/5j+Bf8tJIRtshl8S4OXYLXvgzTtHZqOwpEmlz/V+YQqdbK/fasS0wbZ9zTBohbUX/f8EggMQsrdgtOxm2mkPzn6q2fYKtB3eT7B9gxXiC7O6t6ggEHCs9q4lT4BwI8TqLX2eoJCCmMiuI6NgwKjUydU8bWesq22BwF9IcvpgxlcD1x+wEhBSiHz6bLBQ=="
}
```