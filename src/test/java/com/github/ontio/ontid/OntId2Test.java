package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Digest;
import com.github.ontio.ontid.jwt.JWTCredential;
import com.github.ontio.sdk.wallet.Identity;
import junit.framework.TestCase;

import java.util.*;

public class OntId2Test extends TestCase {
    static long gasLimit = 2000000;
    static long gasPrice = 500;
    static String password = "passwordtest";
    static Account payer, issuerSigner, ownerSigner;
    static Identity issuerIdentity, ownerIdentity;
    static OntId2 issuer, owner, verifier;
    static OntSdk ontSdk;

    static {
        try {
            String ip = "http://polaris1.ont.io";
            String restUrl = ip + ":" + "20334";
            String rpcUrl = ip + ":" + "20336";

            ontSdk = OntSdk.getInstance();
            ontSdk.setRpc(rpcUrl);
            ontSdk.setRestful(restUrl);
            ontSdk.setDefaultConnect(ontSdk.getRestful());
            ontSdk.openWalletFile("wallet.json");
            // set credential contract address
            ontSdk.neovm().credentialRecord().setContractAddress("4f7f159ac4b9913bb185fdf1895705f61b7d0cc6");
            payer = ontSdk.getWalletMgr().getAccount("AbG3ZgFrMK6fqwXWR1WkQ1d1EYVunCwknu", password);
            issuerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity(
                    "did:ont:ARr6ApK24EU7nufND4s1SWpwULHBertpJb");
            issuerSigner = ontSdk.getWalletMgr().getAccount(issuerIdentity.ontid, password,
                    issuerIdentity.controls.get(0).getSalt());
            ownerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity(
                    "did:ont:AL6r48EwwzTRwc8Cyc3BB4R8bn2NYGmKYD");
            ownerSigner = ontSdk.getWalletMgr().getAccount(ownerIdentity.ontid, password,
                    ownerIdentity.controls.get(0).getSalt());
            issuer = new OntId2(issuerIdentity.ontid, issuerSigner,
                    ontSdk.neovm().credentialRecord(), ontSdk.nativevm().ontId());
            owner = new OntId2(ownerIdentity.ontid, ownerSigner,
                    ontSdk.neovm().credentialRecord(), ontSdk.nativevm().ontId());
            // verifier may not own ontId and signer
            verifier = new OntId2("", null,
                    ontSdk.neovm().credentialRecord(), ontSdk.nativevm().ontId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testGenSignReq() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            SignRequest signReq = issuer.genSignReq(subject, ProofPurpose.assertionMethod, true);
            assertNotNull(signReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifySignReq() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            SignRequest signReq = owner.genSignReq(subject, ProofPurpose.assertionMethod, true);
            assertTrue(issuer.verifySignReq(signReq));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCreateCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            assertNotNull(credential);
            assertNotNull(credential.proof);
            assertNotNull(credential.proof.hex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCreateCredV2() {
        try {
            Map<String, Object> subject = new TreeMap<>();
            Map<String, Object> profile = new TreeMap<>();
            Map<String, Object> library = new TreeMap<>();
            Map<String, Object> dna1 = new TreeMap<>();
            Map<String, Object> dna2 = new TreeMap<>();
            Map<String, Object> dna3 = new TreeMap<>();
            profile.put("id","76561197960434622");
            profile.put("name", "al");
            profile.put("icon", "https://avatars.steamstatic.com/fdaef0111714471445bb422b8021d3a887c86a99_full.jpg");
            profile.put("lv", "10");
            profile.put("estValue", "10000");
            profile.put("badge", "13");
            profile.put("country", "US");
            profile.put("joined", "2015");
            library.put("game",542);
            library.put("totalPlay","4.5K h");
            library.put("recentPlay","64.5h");
            dna1.put("tag","RPG");
            dna1.put("percent","40%");
            dna2.put("tag","Open World");
            dna2.put("percent","20%");
            dna3.put("tag","Other");
            dna3.put("percent","10%");
            List<Map<String, Object>> dnaList = Arrays.asList(dna1, dna2, dna3);
            subject.put("profile",profile);
            subject.put("library",library);
            subject.put("dna",dnaList);
            subject.put("id",ownerIdentity.ontid);

            System.out.println(JSON.toJSONString(subject, SerializerFeature.MapSortField));

            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredentialV2 credential = issuer.createCredV2(null, new String[]{"claim:steam_auth"}, issuerIdentity.ontid, ownerIdentity.ontid,
                    subject, expiration, ProofPurpose.assertionMethod, payer, 20000, 500, ontSdk);
            assertNotNull(credential);
            assertNotNull(credential.proof);
            assertNotNull(credential.proof.proofValue);
            System.out.println(JSON.toJSONString(credential));
            System.out.println(credential.evidence.get(0).id);
            System.out.println(Helper.toHexString(credential.genNeedSignData()));
            System.out.println(new String(credential.genNeedSignData()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifyCredV2() throws Exception {
        String credStr = "{\"@context\":[\"https://www.w3.org/ns/credentials/v2\",\"https://ontid.ont.io/credentials/v2\"],\"credentialStatus\":{\"id\":\"did:ont:contract:4f7f159ac4b9913bb185fdf1895705f61b7d0cc6\",\"statusPurpose\":\"revocation\",\"statusReference\":\"urn:uuid:7a4ac1dd-a6d7-4f42-8ee2-6daae21780d4\",\"type\":\"BlockchainSmartContractStatus\"},\"credentialSubject\":{\"library\":{\"game\":129,\"recentPlay\":\"0h\",\"totalPlay\":\"998.7h\"},\"dna\":[{\"tag\":\"Adventure\",\"percent\":0.22},{\"tag\":\"Action\",\"percent\":0.218},{\"tag\":\"Sports\",\"percent\":0.129},{\"tag\":\"Simulation\",\"percent\":0.121},{\"tag\":\"Free to Play\",\"percent\":0.119}],\"profile\":{\"badge\":6,\"country\":\"CN\",\"estValue\":2413.57,\"joined\":\"2015\",\"icon\":\"https://avatars.steamstatic.com/77cf08789570a5a970c581032a7ca5fe5323d01a_full.jpg\",\"name\":\"Someday_Even\",\"lv\":\"10\",\"id\":\"76561198262890532\"},\"detail\":[{\"game\":\"PUBG: BATTLEGROUNDS\",\"recentPlay\":\"0h\",\"totalPlay\":\"266.4h\"},{\"game\":\"Grand Theft Auto V Legacy\",\"recentPlay\":\"0h\",\"totalPlay\":\"171h\"},{\"game\":\"NBA 2K19\",\"recentPlay\":\"0h\",\"totalPlay\":\"141.5h\"},{\"game\":\"NBA 2K18\",\"recentPlay\":\"0h\",\"totalPlay\":\"101.5h\"},{\"game\":\"DiRT 3 Complete Edition\",\"recentPlay\":\"0h\",\"totalPlay\":\"63.7h\"},{\"game\":\"NBA 2K23\",\"recentPlay\":\"0h\",\"totalPlay\":\"49.8h\"},{\"game\":\"Dota 2\",\"recentPlay\":\"0h\",\"totalPlay\":\"27h\"},{\"game\":\"Monster Train\",\"recentPlay\":\"0h\",\"totalPlay\":\"24.6h\"},{\"game\":\"NBA 2K16\",\"recentPlay\":\"0h\",\"totalPlay\":\"20.1h\"},{\"game\":\"It Takes Two\",\"recentPlay\":\"0h\",\"totalPlay\":\"19.7h\"},{\"game\":\"Rise of the Tomb Raider\",\"recentPlay\":\"0h\",\"totalPlay\":\"16.9h\"},{\"game\":\"Revenge on gold diggers \",\"recentPlay\":\"0h\",\"totalPlay\":\"15.3h\"},{\"game\":\"隐形守护者 The Invisible Guardian\",\"recentPlay\":\"0h\",\"totalPlay\":\"13.2h\"},{\"game\":\"Hogwarts Legacy\",\"recentPlay\":\"0h\",\"totalPlay\":\"10.7h\"},{\"game\":\"Little Nightmares\",\"recentPlay\":\"0h\",\"totalPlay\":\"9.1h\"},{\"game\":\"Hypnosis Card 2 Happy Life\",\"recentPlay\":\"0h\",\"totalPlay\":\"8.8h\"},{\"game\":\"Lethal Company\",\"recentPlay\":\"0h\",\"totalPlay\":\"7.3h\"},{\"game\":\"Grand Theft Auto IV: The Complete Edition\",\"recentPlay\":\"0h\",\"totalPlay\":\"4.7h\"},{\"game\":\"Biped\",\"recentPlay\":\"0h\",\"totalPlay\":\"4.2h\"},{\"game\":\"428 Shibuya Scramble\",\"recentPlay\":\"0h\",\"totalPlay\":\"4.1h\"},{\"game\":\"Senren＊Banka\",\"recentPlay\":\"0h\",\"totalPlay\":\"3.6h\"},{\"game\":\"Sea of Thieves\",\"recentPlay\":\"0h\",\"totalPlay\":\"3h\"},{\"game\":\"Subnautica\",\"recentPlay\":\"0h\",\"totalPlay\":\"2.7h\"},{\"game\":\"Wallpaper Engine\",\"recentPlay\":\"0h\",\"totalPlay\":\"2.6h\"},{\"game\":\"The Witcher 3: Wild Hunt\",\"recentPlay\":\"0h\",\"totalPlay\":\"1.2h\"},{\"game\":\"The Elder Scrolls V: Skyrim Special Edition\",\"recentPlay\":\"0h\",\"totalPlay\":\"1.1h\"},{\"game\":\"Forza Horizon 4\",\"recentPlay\":\"0h\",\"totalPlay\":\"0.8h\"},{\"game\":\"Mortal Kombat 11\",\"recentPlay\":\"0h\",\"totalPlay\":\"0.7h\"},{\"game\":\"METAL SLUG\",\"recentPlay\":\"0h\",\"totalPlay\":\"0.6h\"},{\"game\":\"Brothers - A Tale of Two Sons\",\"recentPlay\":\"0h\",\"totalPlay\":\"0.5h\"},{\"game\":\"Need for Speed™ Heat \",\"recentPlay\":\"0h\",\"totalPlay\":\"0.3h\"},{\"game\":\"HITMAN™ 2\",\"recentPlay\":\"0h\",\"totalPlay\":\"0.3h\"},{\"game\":\"METAL SLUG X\",\"recentPlay\":\"0h\",\"totalPlay\":\"0.2h\"},{\"game\":\"NEEDY STREAMER OVERLOAD\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Sword and Fairy 5 Prequel\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"There Is No Game: Wrong Dimension\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Monument Valley\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Brotato\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Muse Dash\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Fury Unleashed\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Sword and Fairy 6\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Agent A: A puzzle in disguise\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"DISTRAINT 2\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"The Room 4: Old Sins\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Ori and the Blind Forest\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Sizeable\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Assassin's Creed Odyssey\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Counter-Strike: Source\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"METAL SLUG 3\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Euro Truck Simulator 2\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Darkwood\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Sword and Fairy 4\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Mr. Pumpkin 2: Kowloon walled city\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Hellslave\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Carto\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Marvel's Guardians of the Galaxy\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Danganronpa V3: Killing Harmony\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Undertale\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Little Nightmares Enhanced Edition\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Monument Valley 2\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Ultimate Fishing Simulator®\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"The Pedestrian\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Warhammer 40,000: Gladius - Relics of War\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Ori and the Will of the Wisps\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"ASTLIBRA ～生きた証～ Revision\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"STAR WARS Jedi: Fallen Order™ \",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Otaku's Adventure\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Sherlock Holmes: The Devil's Daughter\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"TOEM\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Shelldiver\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Borderlands 2\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"INSIDE\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Little Nightmares II\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Sword and Fairy 7\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"The Last Campfire\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Everhood\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Rusty Lake Paradise\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Spiritfarer®: Farewell Edition\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Down in Bermuda\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"shapez\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"LiEat\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Dark Sector\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"FIGHT KNIGHT\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Nubs!\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Sniper Elite 4\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Slay the Spire\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"A Way Out\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Scheming Through The Zombie Apocalypse: The Beginning\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Soda Crisis\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"What Remains of Edith Finch\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Yuppie Psycho\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Amnesia: The Dark Descent\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Crash Bandicoot™ N. Sane Trilogy\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Detroit: Become Human\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"DISTRAINT: Deluxe Edition\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Sherlock Holmes: Crimes and Punishments\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Metro 2033 Redux\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Omno\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"This War of Mine\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"STEINS;GATE\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Danganronpa Another Episode: Ultra Despair Girls\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Injustice™ 2\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Look Outside\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"The Rewinder\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Riders Republic\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"American Truck Simulator\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Golden Light\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Danganronpa: Trigger Happy Havoc\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Before Your Eyes\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Besiege\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Gorogoa\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"STEINS;GATE 0\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"The Deed\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Mr. Pumpkin Adventure\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Disco Elysium\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Grand Theft Auto V Enhanced\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Black Mesa\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Sword and Fairy 5\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Danganronpa 2: Goodbye Despair\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Darkest Dungeon®\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Helheim Hassle\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Battlefield™ 1\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Tormented Souls\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Party Club\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Beholder\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"AtmaSphere\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Braid, Anniversary Edition\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Mark of the Ninja: Remastered\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"},{\"game\":\"Contraband Police\",\"recentPlay\":\"0h\",\"totalPlay\":\"0h\"}],\"id\":\"did:ont:7654321\"},\"evidence\":[{\"description\":\"Issuance transaction hash on Ontology blockchain\",\"id\":\"6e88774983822d8d2534d7f59cbc412b8549d5d1a1af8c5a4249034f55e4837c\",\"type\":[\"BlockchainTransactionEvidence\"]}],\"id\":\"urn:uuid:7a4ac1dd-a6d7-4f42-8ee2-6daae21780d4\",\"issuer\":\"did:ont:ARr6ApK24EU7nufND4s1SWpwULHBertpJb\",\"proof\":{\"created\":\"2026-04-23T08:33:51Z\",\"cryptosuite\":\"ecdsa-rdfc-2019\",\"proofPurpose\":\"assertionMethod\",\"proofValue\":\"z7ekyvSKbR3uNWaZbLWtLJKCfJmF1AHbdnajtt3xthY3hUmJwVhFvCFEhuMXaSoLEpuhvPCFEGNrZRvn4jMVwb7po\",\"type\":\"DataIntegrityProof\",\"verificationMethod\":\"did:ont:ARr6ApK24EU7nufND4s1SWpwULHBertpJb#keys-1\"},\"type\":[\"VerifiableCredential\",\"claim:x_auth\"],\"validFrom\":\"2026-04-23T08:33:51Z\",\"validUntil\":\"2026-04-24T08:33:51Z\"}";

//        Object sortedMap = toSortedMap(JSONObject.parseObject(credStr));
//        System.out.println(JSON.toJSONString(sortedMap));

        VerifiableCredentialV2 credential = JSONObject.parseObject(credStr, VerifiableCredentialV2.class);
        System.out.println(Helper.toHexString(credential.genNeedSignData()));
        System.out.println(new String(credential.genNeedSignData()));
        String jsonStr = JSON.toJSONString(credential, SerializerFeature.MapSortField);
        System.out.println(jsonStr);
        boolean verified = issuer.verifyCredSignatureV2(credential);
        System.out.println(verified);
    }

    public static Object toSortedMap(Object obj) {
        if (obj instanceof Map) {
            TreeMap<String, Object> sortedMap = new TreeMap<>();
            Map<String, Object> map = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sortedMap.put(entry.getKey(), toSortedMap(entry.getValue()));
            }
            return sortedMap;
        } else if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            List<Object> sortedList = new ArrayList<>();
            for (Object item : list) {
                sortedList.add(toSortedMap(item));
            }
            return sortedList;
        }
        return obj;
    }

    public void testCreateJWTCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            assertNotNull(jwtCred);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCommitCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String txHash = issuer.commitCred(credential, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            assertNotNull(txHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCommitJWTCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String txHash = owner.commitCred(jwtCred, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            assertNotNull(txHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifyCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
            issuer.commitCred(credential, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            Thread.sleep(6000);
            boolean verified = verifier.verifyCred(credibleOntIds, credential);
            assertTrue(verified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifyJWTCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
            issuer.commitCred(jwtCred, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            Thread.sleep(6000);
            boolean verified = verifier.verifyJWTCred(credibleOntIds, jwtCred);
            assertTrue(verified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCreatePresentation() {
        try {
            CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            CredentialSubject subject2 = new CredentialSubject(issuerIdentity.ontid, "iii", "ddd");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential1 = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            VerifiableCredential credential2 = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            ArrayList<String> challenge = new ArrayList<>();
            challenge.add("d1b23d3...3d23d32d2");
            ArrayList<Object> domain = new ArrayList<>();
            domain.add(new String[]{"https://example.com"});
            VerifiablePresentation presentation = owner.createPresentation(new VerifiableCredential[]{credential1,
                            credential2}, null, null, challenge, domain, ownerIdentity.ontid,
                    null, ProofPurpose.assertionMethod);
            assertNotNull(presentation);
            assertNotNull(presentation.proof);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testJWTPresentation() {
        try {
            CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            CredentialSubject subject2 = new CredentialSubject(ownerIdentity.ontid, "iii", "ddd");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred1 = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String jwtCred2 = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String challenge = "d1b23d3...3d23d32d2";
            String[] domain = new String[]{"https://example.com"};
            String jwtPresentation = issuer.createJWTPresentation(new String[]{jwtCred1, jwtCred2}, null,
                    null, ownerIdentity.ontid, challenge, domain, ProofPurpose.assertionMethod);
            assertNotNull(jwtPresentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testPresentationFromOldCred() throws Exception {
        String oldCred = "";
        String challenge = "d1b23d3...3d23d32d2";
        String[] domain = new String[]{"https://example.com"};
        String presentation = owner.createPresentationFromOldCred(new String[]{oldCred}, null,
                null, ownerIdentity.ontid, challenge, domain, ProofPurpose.assertionMethod);
        assertNotNull(presentation);
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(presentation);
        // check jws
//        byte[] needSignData = jwtCred.genNeedSignData();
//        byte[] signature = jwtCred.parseSignature();
//        boolean presentationSigValid = verifier.verifyPubKeyIdSignature(jwtCred.header.kid, needSignData, signature);
//        assertTrue(presentationSigValid);
        String[] credibleOntIds = new String[]{"did:ont:AHzUfrqpNwHBfXA72D9HciNAKuCr83SzDG"};
        for (String vc : jwtCred.payload.vp.verifiableCredential) {
//            assertTrue(ontSdk.nativevm().ontId().verifyCredNotExpired(vc));
//            assertTrue(ontSdk.nativevm().ontId().verifyCredIssuanceDate(vc));
//            assertTrue(ontSdk.nativevm().ontId().verifyCredOntIdCredible(vc, credibleOntIds));
//            assertTrue(ontSdk.nativevm().ontId().verifyCredSignature(vc));
            // use corresponding credential record contract to verify credential status
//            ontSdk.neovm().credentialRecord().setContractAddress("36bb5c053b6b839c8f6b923fe852f91239b9fccc");
//            assertTrue(ontSdk.nativevm().ontId().verifyCredNotRevoked(vc));
        }
    }

    public void testVerifyPresentationProof() {
        try {
            CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            CredentialSubject subject2 = new CredentialSubject(issuerIdentity.ontid, "iii", "ddd");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential1 = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            issuer.commitCred(credential1, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            VerifiableCredential credential2 = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            issuer.commitCred(credential2, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            Thread.sleep(6000);
            ArrayList<String> challenge = new ArrayList<>();
            challenge.add("d1b23d3...3d23d32d2");
            ArrayList<Object> domain = new ArrayList<>();
            domain.add(new String[]{"https://example.com"});
            VerifiablePresentation presentation = owner.createPresentation(new VerifiableCredential[]{credential1,
                            credential2}, null, null, challenge, domain, ownerIdentity.ontid,
                    null, ProofPurpose.assertionMethod);
            boolean presentationVerified = verifier.verifyPresentationProof(presentation, 0);
            assertTrue(presentationVerified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifyJWTPresentation() {
        try {
            CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            CredentialSubject subject2 = new CredentialSubject(ownerIdentity.ontid, "iii", "ddd");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred1 = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            issuer.commitCred(jwtCred1, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            String jwtCred2 = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            issuer.commitCred(jwtCred2, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            Thread.sleep(6000);
            String challenge = "d1b23d3...3d23d32d2";
            String[] domain = new String[]{"https://example.com"};
            String jwtPresentation = issuer.createJWTPresentation(new String[]{jwtCred1, jwtCred2}, null,
                    null, ownerIdentity.ontid, challenge, domain, ProofPurpose.assertionMethod);
            String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
            boolean presentationVerified = verifier.verifyJWTPresentation(credibleOntIds, jwtPresentation);
            assertTrue(presentationVerified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testJSONLDPresentation() throws Exception {
        CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
        CredentialSubject subject2 = new CredentialSubject(issuerIdentity.ontid, "iii", "ddd");
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        VerifiableCredential credential1 = issuer.createCred(null, null, issuerIdentity.ontid,
                subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
        issuer.commitCred(credential1, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
        VerifiableCredential credential2 = issuer.createCred(null, null, issuerIdentity.ontid,
                subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
        issuer.commitCred(credential2, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
        Thread.sleep(6000);
        ArrayList<String> challenge = new ArrayList<>();
        challenge.add("d1b23d3...3d23d32d2");
        ArrayList<Object> domain = new ArrayList<>();
        domain.add(new String[]{"https://example.com"});
        VerifiablePresentation presentation = owner.createPresentation(new VerifiableCredential[]{credential1,
                        credential2}, null, null, challenge, domain, ownerIdentity.ontid,
                null, ProofPurpose.assertionMethod);
        String jsonPresentation = JSON.toJSONString(presentation);
        presentation = JSON.parseObject(jsonPresentation, VerifiablePresentation.class);
        String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
        for (VerifiableCredential c : presentation.verifiableCredential) {
            boolean v = verifier.verifyCred(credibleOntIds, c);
            assertTrue(v);
        }
        // verify each proof
        for (int i = 0; i < presentation.proof.length; i++) {
            boolean proofVerified = verifier.verifyPresentationProof(presentation, i);
            assertTrue(proofVerified);
        }
    }

    public void testVerifyStringJWTCred() throws Exception {
        String[] credibleOntIds = new String[]{"did:ont:TD5ixYQnxmUy7qTmyfKpSmysSnjwTB1Sdi",
                "did:ont:TYNEYu7t8tp439vmcgpJj7CR4kUqJe6ab2"};
        String cred = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6VFlORVl1N3Q4dHA0Mzl2bWNncEpqN0NSNGtVcUplNmFiMiNrZXlzLTEiLCJ0eXAiOiJKV1QifQ==.eyJpc3MiOiJkaWQ6b250OlRZTkVZdTd0OHRwNDM5dm1jZ3BKajdDUjRrVXFKZTZhYjIiLCJleHAiOjE1OTM1MDgxNTksIm5iZiI6MTU5MzQyMTc1OSwiaWF0IjoxNTkzNDIxNzU5LCJqdGkiOiJ1cm46dXVpZDo0YTkyYWRiZC1iOWUyLTQ3ZTQtODk4ZC1hNTQ2NDcyNmRlOTUiLCJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi8vb250aWQub250LmlvL2NyZWRlbnRpYWxzL3YxIiwiY29udGV4dDEiLCJjb250ZXh0MiJdLCJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiUmVsYXRpb25zaGlwQ3JlZGVudGlhbCJdLCJjcmVkZW50aWFsU3ViamVjdCI6W3siaWQiOiJkaWQ6ZXhhbXBsZTplYmZlYjFmNzEyZWJjNmYxYzI3NmUxMmVjMjEiLCJuYW1lIjoiSmF5ZGVuIERvZSIsInNwb3VzZSI6ImRpZDpleGFtcGxlOmMyNzZlMTJlYzIxZWJmZWIxZjcxMmViYzZmMSJ9LHsiaWQiOiJkaWQ6ZXhhbXBsZTpjMjc2ZTEyZWMyMWViZmViMWY3MTJlYmM2ZjEiLCJuYW1lIjoiTW9yZ2FuIERvZSIsInNwb3VzZSI6ImRpZDpleGFtcGxlOmViZmViMWY3MTJlYmM2ZjFjMjc2ZTEyZWMyMSJ9XSwiY3JlZGVudGlhbFN0YXR1cyI6eyJpZCI6IjUyZGYzNzA2ODBkZTE3YmM1ZDQyNjJjNDQ2ZjEwMmEwZWUwZDYzMTIiLCJ0eXBlIjoiQXR0ZXN0Q29udHJhY3QifSwicHJvb2YiOnsiY3JlYXRlZCI6IjIwMjAtMDYtMjlUMDk6MDk6MTlaIiwiZG9tYWluIjoiIiwicHJvb2ZQdXJwb3NlIjoiYXNzZXJ0aW9uTWV0aG9kIn19fQ==.d9aAj9Ip0OadOXnAtlCBRv11KYx6rYF7ViHQIqiLKiOilcoav+huZMSUhZls5r/JfuXOhRGkjXH6CdxljfI3xA==";
        boolean verified = verifier.verifyJWTCred(credibleOntIds, cred);
        assertTrue(verified);
        String presentation = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6VEQ1aXhZUW54bVV5N3FUbXlmS3BTbXlzU25qd1RCMVNkaSNrZXlzLTEiLCJ0eXAiOiJKV1QifQ==.eyJpc3MiOiJkaWQ6b250OlRENWl4WVFueG1VeTdxVG15ZktwU215c1NuandUQjFTZGkiLCJhdWQiOiIiLCJqdGkiOiJ1cm46dXVpZDplMGNlZDM4YS0yNzRiLTQ1ZTctYWZhNS1iYmUyY2U5ZTcxZWIiLCJ2cCI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi8vb250aWQub250LmlvL2NyZWRlbnRpYWxzL3YxIiwiY29udGV4dDEiLCJjb250ZXh0MiJdLCJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiUmVsYXRpb25zaGlwQ3JlZGVudGlhbCJdLCJ2ZXJpZmlhYmxlQ3JlZGVudGlhbCI6WyJleUpoYkdjaU9pSkZVekkxTmlJc0ltdHBaQ0k2SW1ScFpEcHZiblE2VkZsT1JWbDFOM1E0ZEhBME16bDJiV05uY0VwcU4wTlNOR3RWY1VwbE5tRmlNaU5yWlhsekxURWlMQ0owZVhBaU9pSktWMVFpZlE9PS5leUpwYzNNaU9pSmthV1E2YjI1ME9sUlpUa1ZaZFRkME9IUndORE01ZG0xalozQkthamREVWpSclZYRktaVFpoWWpJaUxDSmxlSEFpT2pFMU9UTTFNRGd4TlRrc0ltNWlaaUk2TVRVNU16UXlNVGMxT1N3aWFXRjBJam94TlRrek5ESXhOelU1TENKcWRHa2lPaUoxY200NmRYVnBaRG8wWVRreVlXUmlaQzFpT1dVeUxUUTNaVFF0T0RrNFpDMWhOVFEyTkRjeU5tUmxPVFVpTENKMll5STZleUpBWTI5dWRHVjRkQ0k2V3lKb2RIUndjem92TDNkM2R5NTNNeTV2Y21jdk1qQXhPQzlqY21Wa1pXNTBhV0ZzY3k5Mk1TSXNJbWgwZEhCek9pOHZiMjUwYVdRdWIyNTBMbWx2TDJOeVpXUmxiblJwWVd4ekwzWXhJaXdpWTI5dWRHVjRkREVpTENKamIyNTBaWGgwTWlKZExDSjBlWEJsSWpwYklsWmxjbWxtYVdGaWJHVkRjbVZrWlc1MGFXRnNJaXdpVW1Wc1lYUnBiMjV6YUdsd1EzSmxaR1Z1ZEdsaGJDSmRMQ0pqY21Wa1pXNTBhV0ZzVTNWaWFtVmpkQ0k2VzNzaWFXUWlPaUprYVdRNlpYaGhiWEJzWlRwbFltWmxZakZtTnpFeVpXSmpObVl4WXpJM05tVXhNbVZqTWpFaUxDSnVZVzFsSWpvaVNtRjVaR1Z1SUVSdlpTSXNJbk53YjNWelpTSTZJbVJwWkRwbGVHRnRjR3hsT21NeU56WmxNVEpsWXpJeFpXSm1aV0l4WmpjeE1tVmlZelptTVNKOUxIc2lhV1FpT2lKa2FXUTZaWGhoYlhCc1pUcGpNamMyWlRFeVpXTXlNV1ZpWm1WaU1XWTNNVEpsWW1NMlpqRWlMQ0p1WVcxbElqb2lUVzl5WjJGdUlFUnZaU0lzSW5Od2IzVnpaU0k2SW1ScFpEcGxlR0Z0Y0d4bE9tVmlabVZpTVdZM01USmxZbU0yWmpGak1qYzJaVEV5WldNeU1TSjlYU3dpWTNKbFpHVnVkR2xoYkZOMFlYUjFjeUk2ZXlKcFpDSTZJalV5WkdZek56QTJPREJrWlRFM1ltTTFaRFF5TmpKak5EUTJaakV3TW1Fd1pXVXdaRFl6TVRJaUxDSjBlWEJsSWpvaVFYUjBaWE4wUTI5dWRISmhZM1FpZlN3aWNISnZiMllpT25zaVkzSmxZWFJsWkNJNklqSXdNakF0TURZdE1qbFVNRGs2TURrNk1UbGFJaXdpWkc5dFlXbHVJam9pSWl3aWNISnZiMlpRZFhKd2IzTmxJam9pWVhOelpYSjBhVzl1VFdWMGFHOWtJbjE5ZlE9PS5kOWFBajlJcDBPYWRPWG5BdGxDQlJ2MTFLWXg2cllGN1ZpSFFJcWlMS2lPaWxjb2F2K2h1Wk1TVWhabHM1ci9KZnVYT2hSR2tqWEg2Q2R4bGpmSTN4QT09Il0sInByb29mIjp7ImNyZWF0ZWQiOiIyMDIwLTA2LTI5VDA5OjEwOjEzWiIsInByb29mUHVycG9zZSI6ImFzc2VydGlvbk1ldGhvZCJ9fX0=.K+kvWWkMqOpYXPdaEeeJFyJ5U0P6wY9QcSjMiCNBrEASc0X9zOLdyzrg/+YVYmBVBd9EYPkxPp7ypdY72BT+1g==";
        verified = verifier.verifyJWTPresentation(credibleOntIds, presentation);
        assertTrue(verified);
    }

    public void testVerifyStringCred() throws Exception {
        String[] credibleOntIds = new String[]{"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F",
                "did:ont:THJjPtj9yLy64CteyPAiUCJmbEqekCXkt1"};
        String cred = "{\"@context\":[\"https://www.w3.org/2018/credentials/v1\",\"https://ontid.ont.io/credentials/v1\",\"context1\",\"context2\"],\"id\":\"urn:uuid:43ae4ecd-34ae-43f2-96b1-fe5e8a9859b5\",\"type\":[\"VerifiableCredential\",\"RelationshipCredential\"],\"issuer\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F\",\"issuanceDate\":\"2020-06-29T09:30:39Z\",\"expirationDate\":\"2020-06-30T09:30:39Z\",\"credentialSubject\":[{\"id\":\"did:example:ebfeb1f712ebc6f1c276e12ec21\",\"name\":\"Jayden Doe\",\"spouse\":\"did:example:c276e12ec21ebfeb1f712ebc6f1\"},{\"id\":\"did:example:c276e12ec21ebfeb1f712ebc6f1\",\"name\":\"Morgan Doe\",\"spouse\":\"did:example:ebfeb1f712ebc6f1c276e12ec21\"}],\"credentialStatus\":{\"id\":\"52df370680de17bc5d4262c446f102a0ee0d6312\",\"type\":\"AttestContract\"},\"proof\":{\"type\":\"EcdsaSecp256r1VerificationKey2019\",\"created\":\"2020-06-29T09:30:39Z\",\"domain\":\"\",\"proofPurpose\":\"assertionMethod\",\"verificationMethod\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F#keys-1\",\"hex\":\"07790e7dc69942fc1265d510fb5e7f0194b661a6cb2c802ad55968b4bd5fd7fb23fdf71255356e75eb376415158c25a4dddd81a42f492ce391f34a08b696850d\"}}";
        VerifiableCredential credential = JSON.parseObject(cred, VerifiableCredential.class);
        assertTrue(verifier.verifyCred(credibleOntIds, credential));
        String presentation = "{\"@context\":[\"https://www.w3.org/2018/credentials/v1\",\"https://ontid.ont.io/credentials/v1\",\"context1\",\"context2\"],\"id\":\"urn:uuid:f1bc8840-ebed-4bdf-b9f4-883dc8ff7b3c\",\"type\":[\"VerifiablePresentation\",\"RelationshipCredential\"],\"verifiableCredential\":[{\"@context\":[\"https://www.w3.org/2018/credentials/v1\",\"https://ontid.ont.io/credentials/v1\",\"context1\",\"context2\"],\"id\":\"urn:uuid:43ae4ecd-34ae-43f2-96b1-fe5e8a9859b5\",\"type\":[\"VerifiableCredential\",\"RelationshipCredential\"],\"issuer\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F\",\"issuanceDate\":\"2020-06-29T09:30:39Z\",\"expirationDate\":\"2020-06-30T09:30:39Z\",\"credentialSubject\":[{\"id\":\"did:example:ebfeb1f712ebc6f1c276e12ec21\",\"name\":\"Jayden Doe\",\"spouse\":\"did:example:c276e12ec21ebfeb1f712ebc6f1\"},{\"id\":\"did:example:c276e12ec21ebfeb1f712ebc6f1\",\"name\":\"Morgan Doe\",\"spouse\":\"did:example:ebfeb1f712ebc6f1c276e12ec21\"}],\"credentialStatus\":{\"id\":\"52df370680de17bc5d4262c446f102a0ee0d6312\",\"type\":\"AttestContract\"},\"proof\":{\"type\":\"EcdsaSecp256r1VerificationKey2019\",\"created\":\"2020-06-29T09:30:39Z\",\"domain\":\"\",\"proofPurpose\":\"assertionMethod\",\"verificationMethod\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F#keys-1\",\"hex\":\"07790e7dc69942fc1265d510fb5e7f0194b661a6cb2c802ad55968b4bd5fd7fb23fdf71255356e75eb376415158c25a4dddd81a42f492ce391f34a08b696850d\"}}],\"holder\":\"did:ont:THJjPtj9yLy64CteyPAiUCJmbEqekCXkt1\",\"proof\":[{\"type\":\"EcdsaSecp256r1VerificationKey2019\",\"created\":\"2020-06-29T09:30:43Z\",\"domain\":\"\",\"proofPurpose\":\"assertionMethod\",\"verificationMethod\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F#keys-1\",\"hex\":\"81a05a13e81855ea97173722f9cda697923727e5459886d4e8b582a932983793f7d410cbd5822ee4cea575bc8386eaac9bdccf22a157382751f8271c8171815b\"}]}";
        VerifiablePresentation verifiablePresentation = JSON.parseObject(presentation, VerifiablePresentation.class);
        assertTrue(verifier.verifyPresentationProof(verifiablePresentation, 0));
    }

    public void testGolangSignature() throws Exception {
        byte[] pubKeyData = Helper.hexToBytes("02fe02f80bce49f59f7d34f39fd2a81e8dff91e04d9892a35425a81e0ff12acee7");
        Account account = new Account(false, pubKeyData);
        String original = "asdkhfakfhakfj";
        byte[] needSignData = Digest.sha256(original.getBytes());
        byte[] sig = Helper.hexToBytes("2e663b13811a24a9d65b746a72b788da111bd23460caefa419c7997d46247dc04edeafb399c3d94c33a32df479326a145c9d0cde2c61c8a4d2d3f565671e1757");
        assertTrue(account.verifySignature(needSignData, sig));
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