package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.JSON;
import com.github.ontio.ontid.VerifiableCredential;
import com.github.ontio.ontid.VerifiablePresentation;
import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JWTClaimTest extends TestCase {

    public void testDeserializeToJWTClaim() {
        try {
            String jwtCredential = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVpkUGpGU3FDcXpNQ3" +
                    "FKRFJVZCNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJzdWIiOiJkaWQ6b250OjExMTExMSIsImp0aSI6InVybjp1dWlkOm" +
                    "NhNmM1ZmY1LTlkODMtNGM0Mi05OGVjLTQwYzYxOTFmMWZiNyIsImlzcyI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVp" +
                    "kUGpGU3FDcXpNQ3FKRFJVZCIsIm5iZiI6MTU5MjgxNTUwMCwiaWF0IjoxNTkyODE1NTAwLCJleHAiOjE1OTI5MDE5MDAs" +
                    "InZjIjp7IkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIiwiaHR0cHM6Ly9vb" +
                    "nRpZC5vbnQuaW8vY3JlZGVudGlhbHMvdjEiXSwidHlwZSI6WyJWZXJpZmlhYmxlQ3JlZGVudGlhbCIsIlJlbGF0aW9uc2" +
                    "hpcENyZWRlbnRpYWwiXSwiY3JlZGVudGlhbFN1YmplY3QiOnsibmFtZSI6IkJvYiIsInNwb3VzZSI6IkFsaWNlIn0sImN" +
                    "yZWRlbnRpYWxTdGF0dXMiOnsiaWQiOiI1MmRmMzcwNjgwZGUxN2JjNWQ0MjYyYzQ0NmYxMDJhMGVlMGQ2MzEyIiwidHlw" +
                    "ZSI6IkF0dGVzdENvbnRyYWN0In0sInByb29mIjp7InR5cGUiOiJFY2RzYVNlY3AyNTZyMVZlcmlmaWNhdGlvbktleTIwM" +
                    "TkiLCJjcmVhdGVkIjoiMjAyMC0wNi0yMlQxNjo0NTowMFoiLCJwcm9vZlB1cnBvc2UiOiJhc3NlcnRpb25NZXRob2QifX" +
                    "19.AY4NRJdnb1GUpDdqrbXLupB9cctLwop/YwE9PA7hen7DyJsMh+AOt8x3CrIEss6MXhgsQcuW46sKiZiAIUP8538=";
            String jwtPresentation = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QVZlNHpWWnp0ZW82SG9McGRCd3BLTnREWExq" +
                    "SkJ6QjlmdiNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJzdWIiOiIiLCJqdGkiOiJ1cm46dXVpZDo4MDg3NTE5YS1iYThk" +
                    "LTQ5ODUtYjc3Yi0wYTMxNGM3YmVjMDEiLCJpc3MiOiJkaWQ6b250OkFWZTR6Vlp6dGVvNkhvTHBkQndwS050RFhMakpCe" +
                    "kI5ZnYiLCJuYmYiOjE1OTI4MTU1MzgsImlhdCI6MTU5MjgxNTUzOCwiZXhwIjowLCJhdWQiOlsiaHR0cHM6Ly9leGFtcG" +
                    "xlLmNvbSJdLCJub25jZSI6IiIsInZwIjp7IkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnR" +
                    "pYWxzL3YxIiwiaHR0cHM6Ly9vbnRpZC5vbnQuaW8vY3JlZGVudGlhbHMvdjEiXSwidHlwZSI6WyJWZXJpZmlhYmxlUHJl" +
                    "c2VudGF0aW9uIiwiQ3JlZGVudGlhbE1hbmFnZXJQcmVzZW50YXRpb24iXSwidmVyaWZpYWJsZUNyZWRlbnRpYWwiOlsiZ" +
                    "XlKaGJHY2lPaUpGVXpJMU5pSXNJbXRwWkNJNkltUnBaRHB2Ym5RNlFVbzBRemxoVkZsNFZFZFZhRVZ3WVZwa1VHcEdVM0" +
                    "ZEY1hwTlEzRktSRkpWWkNOclpYbHpMVElpTENKMGVYQWlPaUpLVjFRaWZRPT0uZXlKemRXSWlPaUprYVdRNmIyNTBPakV" +
                    "4TVRFeE1TSXNJbXAwYVNJNkluVnlianAxZFdsa09tTmhObU0xWm1ZMUxUbGtPRE10TkdNME1pMDVPR1ZqTFRRd1l6WXhP" +
                    "VEZtTVdaaU55SXNJbWx6Y3lJNkltUnBaRHB2Ym5RNlFVbzBRemxoVkZsNFZFZFZhRVZ3WVZwa1VHcEdVM0ZEY1hwTlEzR" +
                    "ktSRkpWWkNJc0ltNWlaaUk2TVRVNU1qZ3hOVFV3TUN3aWFXRjBJam94TlRreU9ERTFOVEF3TENKbGVIQWlPakUxT1RJNU" +
                    "1ERTVNREFzSW5aaklqcDdJa0JqYjI1MFpYaDBJanBiSW1oMGRIQnpPaTh2ZDNkM0xuY3pMbTl5Wnk4eU1ERTRMMk55Wld" +
                    "SbGJuUnBZV3h6TDNZeElpd2lhSFIwY0hNNkx5OXZiblJwWkM1dmJuUXVhVzh2WTNKbFpHVnVkR2xoYkhNdmRqRWlYU3dp" +
                    "ZEhsd1pTSTZXeUpXWlhKcFptbGhZbXhsUTNKbFpHVnVkR2xoYkNJc0lsSmxiR0YwYVc5dWMyaHBjRU55WldSbGJuUnBZV" +
                    "3dpWFN3aVkzSmxaR1Z1ZEdsaGJGTjFZbXBsWTNRaU9uc2libUZ0WlNJNklrSnZZaUlzSW5Od2IzVnpaU0k2SWtGc2FXTm" +
                    "xJbjBzSW1OeVpXUmxiblJwWVd4VGRHRjBkWE1pT25zaWFXUWlPaUkxTW1SbU16Y3dOamd3WkdVeE4ySmpOV1EwTWpZeVl" +
                    "6UTBObVl4TURKaE1HVmxNR1EyTXpFeUlpd2lkSGx3WlNJNklrRjBkR1Z6ZEVOdmJuUnlZV04wSW4wc0luQnliMjltSWpw" +
                    "N0luUjVjR1VpT2lKRlkyUnpZVk5sWTNBeU5UWnlNVlpsY21sbWFXTmhkR2x2Ymt0bGVUSXdNVGtpTENKamNtVmhkR1ZrS" +
                    "WpvaU1qQXlNQzB3TmkweU1sUXhOam8wTlRvd01Gb2lMQ0p3Y205dlpsQjFjbkJ2YzJVaU9pSmhjM05sY25ScGIyNU5aWF" +
                    "JvYjJRaWZYMTkuQVk0TlJKZG5iMUdVcERkcXJiWEx1cEI5Y2N0THdvcC9Zd0U5UEE3aGVuN0R5SnNNaCtBT3Q4eDNDckl" +
                    "Fc3M2TVhoZ3NRY3VXNDZzS2laaUFJVVA4NTM4PSIsImV5SmhiR2NpT2lKRlV6STFOaUlzSW10cFpDSTZJbVJwWkRwdmJu" +
                    "UTZRVW8wUXpsaFZGbDRWRWRWYUVWd1lWcGtVR3BHVTNGRGNYcE5RM0ZLUkZKVlpDTnJaWGx6TFRJaUxDSjBlWEFpT2lKS" +
                    "1YxUWlmUT09LmV5SnFkR2tpT2lKMWNtNDZkWFZwWkRwbE1qRmpNREpoWVMxa1lUZzNMVFJrWldFdFlUZzVPUzA0WmpCaV" +
                    "pERmlNREkxWVRNaUxDSnBjM01pT2lKa2FXUTZiMjUwT2tGS05FTTVZVlJaZUZSSFZXaEZjR0ZhWkZCcVJsTnhRM0Y2VFV" +
                    "OeFNrUlNWV1FpTENKdVltWWlPakUxT1RJNE1UVTFNVGtzSW1saGRDSTZNVFU1TWpneE5UVXhPU3dpWlhod0lqb3hOVGt5" +
                    "T1RBeE9UQXdMQ0oyWXlJNmV5SkFZMjl1ZEdWNGRDSTZXeUpvZEhSd2N6b3ZMM2QzZHk1M015NXZjbWN2TWpBeE9DOWpjb" +
                    "VZrWlc1MGFXRnNjeTkyTVNJc0ltaDBkSEJ6T2k4dmIyNTBhV1F1YjI1MExtbHZMMk55WldSbGJuUnBZV3h6TDNZeElsMH" +
                    "NJblI1Y0dVaU9sc2lWbVZ5YVdacFlXSnNaVU55WldSbGJuUnBZV3dpTENKU1pXeGhkR2x2Ym5Ob2FYQkRjbVZrWlc1MGF" +
                    "XRnNJbDBzSW1semMzVmxjaUk2ZXlKdVlXMWxJam9pYVhOemRXVnlJbjBzSW1OeVpXUmxiblJwWVd4VGRXSnFaV04wSWpw" +
                    "YmV5SnBaQ0k2SW1ScFpEcHZiblE2TVRFeE1URXhJaXdpYm1GdFpTSTZJbWhsSWl3aWMzQnZkWE5sSWpvaWMyaGxJbjFkT" +
                    "ENKamNtVmtaVzUwYVdGc1UzUmhkSFZ6SWpwN0ltbGtJam9pTlRKa1pqTTNNRFk0TUdSbE1UZGlZelZrTkRJMk1tTTBORF" +
                    "ptTVRBeVlUQmxaVEJrTmpNeE1pSXNJblI1Y0dVaU9pSkJkSFJsYzNSRGIyNTBjbUZqZENKOUxDSndjbTl2WmlJNmV5SjB" +
                    "lWEJsSWpvaVJXTmtjMkZUWldOd01qVTJjakZXWlhKcFptbGpZWFJwYjI1TFpYa3lNREU1SWl3aVkzSmxZWFJsWkNJNklq" +
                    "SXdNakF0TURZdE1qSlVNVFk2TkRVNk1UbGFJaXdpY0hKdmIyWlFkWEp3YjNObElqb2lZWE56WlhKMGFXOXVUV1YwYUc5a" +
                    "0luMTlmUT09LkFjY2Q0dkJIZHR0R0R1aVFUa3BCVmtLak5SYzh6RWxCOUlvVmZXVjJCbkNjS3pHMGtOT1pjSW9YNldpVW" +
                    "9acG0xWGFvL1lrTi9NUmdSTTloREJFR1B2ND0iXSwicHJvb2YiOnsidHlwZSI6IkVjZHNhU2VjcDI1NnIxVmVyaWZpY2F" +
                    "0aW9uS2V5MjAxOSIsImNyZWF0ZWQiOiIyMDIwLTA2LTIyVDE2OjQ1OjM4WiIsImNoYWxsZW5nZSI6ImQxYjIzZDMuLi4z" +
                    "ZDIzZDMyZDIiLCJkb21haW4iOlsiaHR0cHM6Ly9leGFtcGxlLmNvbSJdLCJwcm9vZlB1cnBvc2UiOiJhc3NlcnRpb25NZ" +
                    "XRob2QifX19.AbQD8FTwRpNeOmzjsUbgeDVKthLHVykxsgCejA8TsHVrx1DhTvOt+K/MY05OsYPLY5iI5DcAoq5zsAzKY" +
                    "eeSoWA=";
            JWTClaim jwtClaim1 = JWTClaim.deserializeToJWTClaim(jwtCredential);
            assertNotNull(jwtClaim1.payload.vc);
            VerifiableCredential credential = VerifiableCredential.deserializeFromJWT(jwtClaim1);
            assertNotNull(credential);
            JWTClaim jwtClaim2 = JWTClaim.deserializeToJWTClaim(jwtPresentation);
            assertNotNull(jwtClaim2.payload.vp);
            VerifiablePresentation presentation = VerifiablePresentation.deserializeFromJWT(jwtClaim2);
            assertNotNull(presentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSerializeToJWTClaim() {
        try {
            String jsonCredential = "{" +
                    "\"@context\":[" +
                    "\"https://www.w3.org/2018/credentials/v1\",\"https://ontid.ont.io/credentials/v1\"" +
                    "]," +
                    "\"id\":\"urn:uuid:2801ef05-0c55-45d8-83a3-dc79e3d055bd\"," +
                    "\"type\":[\"VerifiableCredential\",\"RelationshipCredential\"]," +
                    "\"issuer\":\"did:ont:AJ4C9aTYxTGUhEpaZdPjFSqCqzMCqJDRUd\"," +
                    "\"issuanceDate\":\"2020-06-23T13:55:46Z\"," +
                    "\"expirationDate\":\"2020-06-24T13:55:46Z\"," +
                    "\"credentialSubject\":[" +
                    "{\"id\":\"did:ont:111111\",\"name\":\"Bob\",\"spouse\":\"Alice\"}" +
                    "]," +
                    "\"credentialStatus\":{" +
                    "\"id\":\"52df370680de17bc5d4262c446f102a0ee0d6312\",\"type\":\"AttestContract\"" +
                    "}," +
                    "\"proof\":{\"type\":\"EcdsaSecp256r1VerificationKey2019\",\"created\":\"2020-06-23T13:55:46Z\"," +
                    "\"proofPurpose\":\"assertionMethod\"," +
                    "\"verificationMethod\":\"did:ont:AJ4C9aTYxTGUhEpaZdPjFSqCqzMCqJDRUd#keys-2\"," +
                    "\"hex\":\"015d1fa53d44fc035f7ee3b5637024c67bfc1d253cf11caac38e118650454c5123848aff04589694de" +
                    "ed1e3e08efa863d377d054c20392370b38aa879918fb0690\"}}";
            VerifiableCredential credential = JSON.parseObject(jsonCredential, VerifiableCredential.class);
            // assign jws, in case of transformation will fail
            credential.proof.jws = "aaa";
            JWTClaim jwtClaim = new JWTClaim(credential);
            assertNotNull(jwtClaim.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class CredentialSubject {
    String id;
    String name;
    String spouse;

    public CredentialSubject(String id, String name, String spouse) {
        this.id = id;
        this.name = name;
        this.spouse = spouse;
    }
}