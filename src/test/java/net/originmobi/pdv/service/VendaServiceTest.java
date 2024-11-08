package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.model.Venda;
import net.originmobi.pdv.repository.VendaRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VendaServiceTest {

    @InjectMocks
    private VendaService vendaService;

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private UsuarioService usuarioService;

    private Venda venda;

    private String[] valorParcela;

    private String[] titulos;

    @BeforeEach
    public void inicializa() {
        venda = new Venda();
        venda.setCodigo(1L);
        venda.setSituacao(VendaSituacao.ABERTA);
        String[] valorParcela = {"356.50"};
        String[] titulos = {"2"};
    }

    @Test
    public void testAbreVenda_NovaVenda() {
        venda.setCodigo(null);
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> {
            Venda salvaVenda = invocation.getArgument(0);
            salvaVenda.setCodigo(1L);
            return salvaVenda;
        });

        Long resultado = vendaService.abreVenda(venda);

        assertNotNull(resultado, "A venda aberta deverá ser salva.");
        assertEquals(VendaSituacao.ABERTA, venda.getSituacao());
    }

    @Test
    public void testFechaVenda_VendaAberta() {
        when(vendaRepository.findByCodigoEquals(1L)).thenReturn(venda);
        String resultado = vendaService.fechaVenda(1L, 2L, 713.0, 10.0, 5.0, valorParcela, titulos);

        assertEquals("Venda finalizada com sucesso", resultado);
        assertEquals(VendaSituacao.FECHADA, venda.getSituacao());
    }

    @Test
    public void testQtdAbertos() {
        when(vendaRepository.qtdVendasEmAberto()).thenReturn(1);

        int resultado = vendaService.qtdAbertos();
        assertEquals(1, resultado);
    }
}
