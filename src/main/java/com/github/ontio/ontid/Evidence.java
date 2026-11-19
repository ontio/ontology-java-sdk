package com.github.ontio.ontid;

import java.util.Collections;
import java.util.List;

public class Evidence {
    public String id;
    public List<String> type = Collections.singletonList("BlockchainTransactionEvidence");
    public String description = "Issuance transaction hash on Ontology blockchain";

    public Evidence() {
    }

    public Evidence(String id) {
        if (id.startsWith("ont:tx:")) {
            this.id = id;
        } else {
            this.id = "ont:tx:" + id;
        }
    }
}
