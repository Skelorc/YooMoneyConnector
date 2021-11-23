package connector;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListOperations {

    @SerializedName("operations")
    private List<Operations> list_operations;

    public List<Operations> getList_operations() {
        return list_operations;
    }

    public void setList_operations(List<Operations> list_operations) {
        this.list_operations = list_operations;
    }

    @Override
    public String toString() {
        return "ListOperationsObject{" +
                "list_operations=" + list_operations +
                '}';
    }
}
