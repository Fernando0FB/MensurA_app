package com.example.mensura.data.network;

import com.example.mensura.data.model.AnaliseResponse;
import com.example.mensura.data.model.LoginRequest;
import com.example.mensura.data.model.LoginResponse;
import com.example.mensura.data.model.MensuracaoDTO;
import com.example.mensura.data.model.PagedResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/mensuracoes")
    Call<PagedResponse<MensuracaoDTO>> getMensuracoes(
            @Header("Authorization") String token
    );

    @GET("api/mensuracoes/{id}/analise")
    Call<AnaliseResponse> getAnaliseMensuracao(
            @Path("id") int id,
            @Header("Authorization") String token
    );
}
