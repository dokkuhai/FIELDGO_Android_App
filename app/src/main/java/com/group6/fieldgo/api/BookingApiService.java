// api/BookingApiService.java
package com.group6.fieldgo.api;

import com.group6.fieldgo.model.BookingRequest;
import com.group6.fieldgo.model.BookingResponse;
import com.group6.fieldgo.model.SlotResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingApiService {
    @GET("api/courts/{courtId}/slots")
    Call<SlotResponse> getSlots(
            @Path("courtId") int courtId,
            @Query("date") String date
    );
    @POST("api/bookings")
    Call<BookingResponse> bookSlot(
            @Body BookingRequest request
    );
}