package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Fornecedor;
import net.originmobi.pdv.model.Pagar;
import net.originmobi.pdv.model.PagarParcela;
import net.originmobi.pdv.model.PagarTipo;
import net.originmobi.pdv.repository.PagarRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PagarServiceTest {
	

    @InjectMocks
    private PagarService pagarService;

    @Mock
    private PagarRepository pagarRepo;

    @Mock
    private PagarParcelaService pagarParcelaServ;

    @Mock
    private FornecedorService fornecedores;

    @Mock
    private CaixaService caixas;

    @Mock
    private UsuarioService usuarios;

    @Mock
    private CaixaLancamentoService lancamentos;

    private Fornecedor fornecedor;
    private Pagar pagar;
    private PagarTipo tipo;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        fornecedor = new Fornecedor();
        Timestamp dataCadastro = new Timestamp(System.currentTimeMillis());
        tipo = new PagarTipo("Despesa Geral",dataCadastro);

        pagar = new Pagar("Test Description", 100.0, LocalDate.now(), fornecedor, tipo);
    }
    
    
    @Test
    public void testCadastrarComSucesso() {
        Long codFornecedor = 1L;
        Double valor = 100.0;
        String obs = "Observação";
        LocalDate vencimento = LocalDate.now();
        
        when(fornecedores.busca(codFornecedor)).thenReturn(Optional.of(fornecedor));
        
        String resultado = pagarService.cadastrar(codFornecedor, valor, obs, vencimento, tipo);

        verify(pagarRepo, times(1)).save(any(Pagar.class));
        assertEquals("Despesa lançada com sucesso", resultado);
    }
    
    @Test
    public void testCadastrarComFornecedorInvalido() {
        Long codFornecedor = 1L;
        Double valor = 100.0;
        String obs = "Observação";
        LocalDate vencimento = LocalDate.now();

        when(fornecedores.busca(codFornecedor)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            pagarService.cadastrar(codFornecedor, valor, obs, vencimento, tipo);
        });
    }
    
    @Test
    public void testQuitarComSaldoInsuficiente() {
        Long codparcela = 1L;
        Double vlPago = 500.0;
        Double vldesc = 0.0;
        Double vlacre = 0.0;
        Long codCaixa = 1L;

        PagarParcela parcela = new PagarParcela();
        parcela.setValor_restante(100.0);
        
        Caixa caixa = new Caixa();
        caixa.setValor_total(300.0);  

        when(pagarParcelaServ.busca(codparcela)).thenReturn(Optional.of(parcela));
        when(caixas.busca(codCaixa)).thenReturn(Optional.of(caixa));

        assertThrows(RuntimeException.class, () -> {
            pagarService.quitar(codparcela, vlPago, vldesc, vlacre, codCaixa);
        });
    }
    
    @Test
    public void testQuitarComValorDePagamentoInvalido() {
        Long codparcela = 1L;
        Double vlPago = 150.0;  
        Double vldesc = 0.0;
        Double vlacre = 0.0;
        Long codCaixa = 1L;

        PagarParcela parcela = new PagarParcela();
        parcela.setValor_restante(100.0);

        when(pagarParcelaServ.busca(codparcela)).thenReturn(Optional.of(parcela));

        assertThrows(RuntimeException.class, () -> {
            pagarService.quitar(codparcela, vlPago, vldesc, vlacre, codCaixa);
        });
    }

    
}