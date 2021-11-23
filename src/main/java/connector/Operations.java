package connector;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Operations {

    @SerializedName("operation_id")
    private String operation_id;
    @SerializedName("status")
    private String status;
    @SerializedName("datetime")
    private String datetime;
    @SerializedName("title")
    private String title;
    @SerializedName("direction")
    private String direction;
    @SerializedName("amount")
    private String amount;
    @SerializedName("label")
    private String label;
    @SerializedName("type")
    private String type;
}
