// api/BookingApiService.java
package com.group6.fieldgo.api;

import com.group6.fieldgo.model.SlotResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingApiService {
    @GET("api/courts/{courtId}/slots")
    Call<SlotResponse> getSlots(
            @Path("courtId") int courtId,
            @Query("date") String date
    );
}