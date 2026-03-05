package com.banco.cliente.controller;

import com.banco.cliente.dto.ClienteDTO;
import com.banco.cliente.exception.GlobalExceptionHandler;
import com.banco.cliente.exception.ResourceNotFoundException;
import com.banco.cliente.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteController — Tests Unitarios")
class ClienteControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @Mock ClienteService clienteService;
    @InjectMocks ClienteController clienteController;

    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        mapper = new ObjectMapper();

        clienteDTO = ClienteDTO.builder()
                .id(1L).nombre("Jose Lema").genero("Masculino").edad(35)
                .identificacion("1234567890").direccion("Otavalo sn y principal")
                .telefono("098254785").clienteId("CL001").contrasena("1234").estado(true).build();
    }

    @Test
    @DisplayName("POST /api/clientes → 201 con datos del cliente creado")
    void debeCrearCliente_Return201() throws Exception {
        when(clienteService.crear(any())).thenReturn(clienteDTO);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Jose Lema"))
                .andExpect(jsonPath("$.data.clienteId").value("CL001"));

        verify(clienteService).crear(any());
    }

    @Test
    @DisplayName("GET /api/clientes → 200 con lista de clientes")
    void debeListarClientes_Return200() throws Exception {
        ClienteDTO c2 = ClienteDTO.builder().id(2L).nombre("Marianela Montalvo")
                .clienteId("CL002").identificacion("0987654321").estado(true).build();
        when(clienteService.listarTodos()).thenReturn(List.of(clienteDTO, c2));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].nombre").value("Jose Lema"));
    }

    @Test
    @DisplayName("GET /api/clientes/99 → 404 cuando no existe")
    void debeRetornar404_ClienteNoExiste() throws Exception {
        when(clienteService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Cliente", "id", 99L));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("DELETE /api/clientes/1 → 200 eliminación exitosa")
    void debeEliminarCliente_Return200() throws Exception {
        doNothing().when(clienteService).eliminar(1L);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(clienteService).eliminar(1L);
    }

    @Test
    @DisplayName("POST /api/clientes → 400 cuando nombre está vacío")
    void debeRetornar400_NombreVacio() throws Exception {
        ClienteDTO invalido = ClienteDTO.builder()
                .nombre("").identificacion("123").clienteId("X").contrasena("pwd").estado(true).build();

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
