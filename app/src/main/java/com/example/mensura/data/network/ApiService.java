package com.example.mensura.data.network;

import com.example.mensura.data.model.AnaliseResponse;
import com.example.mensura.data.model.LoginRequest;
import com.example.mensura.data.model.LoginResponse;
import com.example.mensura.data.model.MensuracaoCreateDTO;
import com.example.mensura.data.model.MensuracaoDTO;
import com.example.mensura.data.model.PacienteDTO;
import com.example.mensura.data.model.PagedResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/mensuracoes")
    Call<PagedResponse<MensuracaoDTO>> getMensuracoes(
            @Header("Authorization") String token,
            @Query("pacienteNome") String pacienteNome,
            @Query("articulacao") String articulacao,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/pacientes")
    Call<PagedResponse<PacienteDTO>> getPacientes(
            @Header("Authorization") String token,
            @Query("pacienteNome") String pacienteNome,        // opcional
            @Query("page") Integer page,                 // se usar paginação
            @Query("size") Integer size
    );

    @GET("api/mensuracoes/{id}/analise")
    Call<AnaliseResponse> getAnaliseMensuracao(
            @Path("id") int id,
            @Header("Authorization") String token
    );

    @POST("api/pacientes")
    Call<PacienteDTO> createPaciente(
            @Header("Authorization") String token,
            @Body PacienteDTO paciente
    );

    @POST("api/mensuracoes")
    Call<MensuracaoDTO> createMensuracao(
            @Header("Authorization") String token,
            @Body MensuracaoCreateDTO mensuracao
    );
}
