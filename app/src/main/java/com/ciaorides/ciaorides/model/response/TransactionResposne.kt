package com.ciaorides.ciaorides.model.response

import java.io.Serializable

data class GetTransactionResposne(
    var status : Boolean,
    var total_amount : Int,
    var transactions : List<Transactions>
) : Serializable {
    data class Transactions (
        var created_date : String,
        var amount : Float,
        var transaction_type : String
    ): Serializable
}
