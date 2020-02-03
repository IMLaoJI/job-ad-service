Deploy to jobroom-dev

```bash
sed -e "s,TARGET,dev,g" <  deployment-config.yml | oc -n jobroom-dev apply -f -
```
