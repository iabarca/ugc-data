
package com.github.iabarca.ugc;

import java.util.List;

public class JsonUgcResponse {

    private List<String> COLUMNS;
    private List<List<Object>> DATA;

    public List<String> getColumns() {
        return COLUMNS;
    }

    public List<List<Object>> getData() {
        return DATA;
    }

    @Override
    public String toString() {
        return "JsonResponse [columns=" + COLUMNS + ", data=" + DATA + "]";
    }

}
