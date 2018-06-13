/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.Struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public class NeoVmDemo {
    public static String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String abi ="{\"hash\":\"0x638119357dd210e0d7661f779526690af5e3cf8e\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"TestMap\",\"parameters\":[],\"returntype\":\"Any\"},{\"name\":\"DeserializeMap\",\"parameters\":[{\"name\":\"param\",\"type\":\"Map\"}],\"returntype\":\"Any\"},{\"name\":\"TestStruct\",\"parameters\":[],\"returntype\":\"Any\"},{\"name\":\"DeserializeStruct\",\"parameters\":[{\"name\":\"param\",\"type\":\"Struct\"}],\"returntype\":\"Any\"}],\"events\":[]}";
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            Account acct1 = new Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);
            Account acct2 = new Account(Helper.hexToBytes(privatekey2), ontSdk.defaultSignScheme);
            Account acct = new Account(Helper.hexToBytes(privatekey0), ontSdk.defaultSignScheme);

            System.out.println("hello:"+Helper.toHexString("hello".getBytes()));
            System.out.println("world:"+Helper.toHexString("world".getBytes()));
            System.out.println("key:"+Helper.toHexString("key".getBytes()));
            System.out.println("claimId:"+Helper.toHexString("claimId".getBytes()));
            System.out.println("claimid:"+Helper.toHexString("claimid".getBytes()));
            System.out.println("name:"+Helper.toHexString("name".getBytes()));
            //System.exit(0);
            if(false){

                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "TestMap";
                AbiFunction func = abiinfo.getFunction(name);
                func.name = name;
                System.out.println(func);
                func.setParamsValue();

                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("638119357dd210e0d7661f779526690af5e3cf8e"),null,null,0,0,func, true);
                System.out.println(obj);

            }
            //82 01 00 05 68656c6c6f 00 05 776f726c64

            if(false){
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "DeserializeMap";
                AbiFunction func = abiinfo.getFunction(name);
                func.name = name;
                Map map = new HashMap<>();
//                map.put("hello","world");
                map.put("key",100);
//                map.put("a","worlda");
//                map.put("b",155);
//                map.put("c",100000);
                System.out.println(func);
                func.setParamsValue(map);

                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("638119357dd210e0d7661f779526690af5e3cf8e"),null,null,0,0,func, true);
                System.out.println(obj);

            }

            //80 02 00 0164 00 07 636c61696d6964
            if(true){
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "TestStruct";
                AbiFunction func = abiinfo.getFunction(name);

                System.out.println(func);
                func.setParamsValue();

                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("638119357dd210e0d7661f779526690af5e3cf8e"),null,null,0,0,func, true);
                System.out.println(obj);

            }
            //80 02 00 0164 00 07 636c61696d6964
            //80 02 02 0164 00 07 636c61696d6964
            if(true){
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "DeserializeStruct";
                AbiFunction func = abiinfo.getFunction(name);

                System.out.println(func);
                func.setParamsValue(new Struct().add(100,"claimid"));

                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("638119357dd210e0d7661f779526690af5e3cf8e"),null,null,0,0,func, true);
                System.out.println(obj);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://139.219.108.204";
        String ip = "http://127.0.0.1";
//        String ip = "http://101.132.193.149";
//        String ip = "http://polaris1.ont.io";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.openWalletFile("nep5.json");


        return wm;
    }
}
