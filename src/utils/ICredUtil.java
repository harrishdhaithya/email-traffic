package utils;

import microsoft.exchange.webservices.data.credential.ExchangeCredentials;

public interface ICredUtil {
    public ExchangeCredentials getRandomCredPair();
    public String getRandomReciever();
}
