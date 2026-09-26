package academico;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import javax.swing.JFormattedTextField;

public class Sistema extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	// Componentes - Dados Pessoais
	private JFormattedTextField txtRgm;
	private JTextField txtNome;
	private JFormattedTextField txtData;
	private JFormattedTextField txtCpf;
	private JTextField txtEmail;
	private JTextField txtEnd;
	private JTextField txtMunicipio;
	private JComboBox<String> cbUf;
	private JFormattedTextField txtCell;
	
	// Componentes - Curso
	private JFormattedTextField txtRgmCurso;
	private JComboBox<String> cbCurso;
	private JComboBox<String> cbCampus;
	private JRadioButton rbMatutino, rbVespertino, rbNoturno;
	private ButtonGroup bgPeriodo;
	
	// Componentes - Notas e Faltas
	private JFormattedTextField txtRgmNotas;
	private JTextField txtMostraNome;
	private JTextField txtMostraCurso;
	private JComboBox<String> cbDisc;
	private JComboBox<String> cbSemestre;
	private JComboBox<String> cbA1;
	private JComboBox<String> cbA2;
	private JComboBox<String> cbAF;
	private JComboBox<String> cbFaltas;
	
	// Componentes - Boletim
	private JTable tableBoletim;
	private DefaultTableModel tableModel;

	// Memória Temporária para Edição em Lote
	private Map<String, NotasTmp> memoriaNotasTemporarias = new HashMap<>();
	private String ultimaDisciplinaSelecionada = "";
	private boolean ignorarEventoTrocaMateria = false;

	private static class NotasTmp {
		String a1;
		String a2;
		String af;
		String faltas; 
		NotasTmp(String a1, String a2, String af, String faltas) {
			this.a1 = a1;
			this.a2 = a2;
			this.af = af;
			this.faltas = faltas;
		}
	}

	// Conexão com o Banco de Dados
	private Connection obterConexao() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/sistema_academico";
		String usuario = "root"; 
		String senha = "admin1004"; 
		return DriverManager.getConnection(url, usuario, senha);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sistema frame = new Sistema();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Sistema() throws Exception {
		setTitle("Sistema Acadêmico");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 560);
		setLocationRelativeTo(null);
		
		
		// BARRA DE MENU SUPERIOR
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		// MENU: ALUNO
		JMenu mnAluno = new JMenu("Aluno");
		menuBar.add(mnAluno);
		
		JMenuItem mntmSalvarA = new JMenuItem("Salvar");
		mntmSalvarA.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		mntmSalvarA.addActionListener(e -> salvarAluno());
		mnAluno.add(mntmSalvarA);
		
		JMenuItem mntmAlterarA = new JMenuItem("Alterar");
		mntmAlterarA.addActionListener(e -> alterarAluno());
		mnAluno.add(mntmAlterarA);
		
		JMenuItem mntmConsultarA = new JMenuItem("Consultar (Aba Dados)");
		mntmConsultarA.addActionListener(e -> consultarAluno(txtRgm.getText().trim(), "dados"));
		mnAluno.add(mntmConsultarA);
		
		JMenuItem mntmExcluirA = new JMenuItem("Excluir");
		mntmExcluirA.addActionListener(e -> excluirAluno());
		mnAluno.add(mntmExcluirA);
		
		mnAluno.add(new JSeparator());
		
		JMenuItem mntmSair = new JMenuItem("Sair");
		mntmSair.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK));
		mntmSair.addActionListener(e -> System.exit(0));
		mnAluno.add(mntmSair);
		
		// MENU: NOTAS E FALTAS
		JMenu mnNotas = new JMenu("Notas e Faltas");
		menuBar.add(mnNotas);
		
		JMenuItem mntmSalvarN = new JMenuItem("Salvar Tudo no Banco");
		mntmSalvarN.addActionListener(e -> salvarTodasAsNotasNoBanco());
		mnNotas.add(mntmSalvarN);
		
		JMenuItem mntmExcluirN = new JMenuItem("Excluir Matéria Atual");
		mntmExcluirN.addActionListener(e -> excluirNota());
		mnNotas.add(mntmExcluirN);
		
		JMenuItem mntmConsultarN = new JMenuItem("Consultar");
		mntmConsultarN.addActionListener(e -> buscarDadosParaNotas());
		mnNotas.add(mntmConsultarN);

		// MENU: AJUDA 
		JMenu mnAjuda = new JMenu("Ajuda");
		menuBar.add(mnAjuda);

		JMenuItem mntmSobre = new JMenuItem("Sobre");
		mntmSobre.addActionListener(e -> exibirPainelSobre());
		mnAjuda.add(mntmSobre);
		
		contentPane = new JPanel();
		contentPane.setBackground(new Color(225, 225, 225));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(20, 20, 645, 450);
		contentPane.add(tabbedPane);
		
		
		// Primeira ABA: DADOS PESSOAIS
		
		JPanel panelDados = new JPanel();
		panelDados.setLayout(null);
		tabbedPane.addTab("Dados Pessoais", null, panelDados, null);
		
		// Componentes Visuais dos Dados
		JLabel lblRgm = new JLabel("RGM");
		lblRgm.setBounds(15, 20, 40, 25);
		panelDados.add(lblRgm);
		
		txtRgm = new JFormattedTextField(new MaskFormatter("#########"));
		txtRgm.setBounds(60, 20, 120, 25);
		panelDados.add(txtRgm);
		
		JLabel lblNome = new JLabel("Nome");
		lblNome.setBounds(200, 20, 40, 25);
		panelDados.add(lblNome);
		
		txtNome = new JTextField();
		txtNome.setBounds(245, 20, 360, 25);
		panelDados.add(txtNome);
		
		JLabel lblData = new JLabel("Data de Nasc.");
		lblData.setBounds(15, 65, 130, 25);
		panelDados.add(lblData);
		
		txtData = new JFormattedTextField(new MaskFormatter("##/##/####"));
		txtData.setBounds(110, 65, 120, 25);
		panelDados.add(txtData);
		
		JLabel lblCpf = new JLabel("CPF");
		lblCpf.setBounds(260, 65, 40, 25);
		panelDados.add(lblCpf);
		
		txtCpf = new JFormattedTextField(new MaskFormatter("###.###.###-##"));
		txtCpf.setBounds(300, 65, 305, 25);
		panelDados.add(txtCpf);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(15, 110, 40, 25);
		panelDados.add(lblEmail);
		
		txtEmail = new JTextField();
		txtEmail.setBounds(60, 110, 350, 25);
		panelDados.add(txtEmail);
		
		JLabel lblSufixoEmail = new JLabel("@cs.unicid.edu.br");
		lblSufixoEmail.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSufixoEmail.setForeground(new Color(100, 100, 100));
		lblSufixoEmail.setBounds(415, 110, 175, 25);
		panelDados.add(lblSufixoEmail);
		
		JLabel lblEnd = new JLabel("End.");
		lblEnd.setBounds(15, 155, 40, 25);
		panelDados.add(lblEnd);
		
		txtEnd = new JTextField();
		txtEnd.setBounds(60, 155, 545, 25);
		panelDados.add(txtEnd);
		
		JLabel lblMunicipio = new JLabel("Município");
		lblMunicipio.setBounds(15, 200, 60, 25);
		panelDados.add(lblMunicipio);
		
		txtMunicipio = new JTextField();
		txtMunicipio.setBounds(80, 200, 150, 25);
		panelDados.add(txtMunicipio);
		
		JLabel lblUf = new JLabel("UF");
		lblUf.setBounds(245, 200, 20, 25);
		panelDados.add(lblUf);
		
		cbUf = new JComboBox<>(new String[] {"SP", "AC", "AM", "BA", "CE", "DF", "ES", "GO", "MG", "PR", "RJ", "RS", "SC"});
		cbUf.setBounds(270, 200, 60, 25);
		panelDados.add(cbUf);
		
		JLabel lblCell = new JLabel("Celular");
		lblCell.setBounds(350, 200, 50, 25);
		panelDados.add(lblCell);
		
		txtCell = new JFormattedTextField(new MaskFormatter("(##)#####-####"));
		txtCell.setBounds(410, 200, 195, 25);
		panelDados.add(txtCell);

		
		// Segunda ABA: CURSO
		
		JPanel panelCurso = new JPanel();
		panelCurso.setLayout(null);
		tabbedPane.addTab("Curso", null, panelCurso, null);
		
		JLabel lblRgmC = new JLabel("Pesquisar RGM");
		lblRgmC.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblRgmC.setBounds(20, 20, 95, 25);
		panelCurso.add(lblRgmC);
		
		txtRgmCurso = new JFormattedTextField(new MaskFormatter("#########"));
		txtRgmCurso.setBounds(120, 20, 120, 25);
		panelCurso.add(txtRgmCurso);
		
		JButton btnPesquisarCurso = new JButton("Buscar Estudante");
		btnPesquisarCurso.setBounds(250, 20, 160, 25);
		btnPesquisarCurso.addActionListener(e -> consultarAluno(txtRgmCurso.getText().trim(), "curso"));
		panelCurso.add(btnPesquisarCurso);
		
		JSeparator sepCurso = new JSeparator();
		sepCurso.setBounds(20, 60, 585, 2);
		panelCurso.add(sepCurso);
		
		JLabel lblCurso = new JLabel("Curso");
		lblCurso.setBounds(20, 80, 60, 25);
		panelCurso.add(lblCurso);
		
		cbCurso = new JComboBox<>(new String[] {"Análise e Desenvolvimento de Sistemas", "Ciência da Computação", "Engenharia de Software"});
		cbCurso.setBounds(90, 80, 515, 25);
		panelCurso.add(cbCurso);
		
		JLabel lblCampus = new JLabel("Campus");
		lblCampus.setBounds(20, 130, 60, 25);
		panelCurso.add(lblCampus);
		
		cbCampus = new JComboBox<>(new String[] {"Tatuapé", "Vila-Lobos"}); 
		cbCampus.setBounds(90, 130, 515, 25);
		panelCurso.add(cbCampus);
		
		JLabel lblPeriodo = new JLabel("Período");
		lblPeriodo.setBounds(20, 180, 60, 25);
		panelCurso.add(lblPeriodo);
		
		rbMatutino = new JRadioButton("Matutino");
		rbMatutino.setBounds(90, 180, 100, 25);
		rbVespertino = new JRadioButton("Vespertino");
		rbVespertino.setBounds(210, 180, 100, 25);
		rbNoturno = new JRadioButton("Noturno");
		rbNoturno.setSelected(true);
		rbNoturno.setBounds(330, 180, 100, 25);
		
		bgPeriodo = new ButtonGroup();
		bgPeriodo.add(rbMatutino);
		bgPeriodo.add(rbVespertino);
		bgPeriodo.add(rbNoturno);
		
		panelCurso.add(rbMatutino);
		panelCurso.add(rbVespertino);
		panelCurso.add(rbNoturno);
		
		JPanel panelBotoesCurso = new JPanel();
		panelBotoesCurso.setBounds(20, 260, 595, 80);
		panelCurso.add(panelBotoesCurso);
		panelBotoesCurso.setLayout(null);
		
		JButton btnSairC = new JButton("<html><center>⏻<br>Sair</center></html>");
		btnSairC.setBackground(new Color(200, 50, 50));
		btnSairC.setForeground(Color.WHITE);
		btnSairC.setBounds(10, 10, 85, 60);
		btnSairC.addActionListener(e -> System.exit(0));
		panelBotoesCurso.add(btnSairC);
		
		JButton btnSalvarC = new JButton("<html><center>💾<br>Salvar</center></html>");
		btnSalvarC.setBounds(110, 10, 85, 60);
		btnSalvarC.addActionListener(e -> salvarAluno());
		panelBotoesCurso.add(btnSalvarC);
		
		JButton btnAlterarC = new JButton("<html><center>📝<br>Alterar</center></html>");
		btnAlterarC.setBounds(210, 10, 85, 60);
		btnAlterarC.addActionListener(e -> alterarAluno());
		panelBotoesCurso.add(btnAlterarC);
		
		JButton btnExcluirC = new JButton("<html><center>🗑<br>Excluir</center></html>");
		btnExcluirC.setBounds(310, 10, 85, 60);
		btnExcluirC.addActionListener(e -> excluirAluno());
		panelBotoesCurso.add(btnExcluirC);
		
		JButton btnConsultarC = new JButton("<html><center>🔍<br>Consultar</center></html>");
		btnConsultarC.setBounds(410, 10, 85, 60);
		btnConsultarC.addActionListener(e -> consultarAluno(txtRgmCurso.getText().trim(), "curso"));
		panelBotoesCurso.add(btnConsultarC);

		// Terceira ABA: NOTAS E FALTAS
		
		JPanel panelNotas = new JPanel();
		panelNotas.setLayout(null);
		tabbedPane.addTab("Notas e Faltas", null, panelNotas, null);
		
		JLabel lblRgmN = new JLabel("RGM");
		lblRgmN.setBounds(20, 15, 40, 25);
		panelNotas.add(lblRgmN);
		
		txtRgmNotas = new JFormattedTextField(new MaskFormatter("#########"));
		txtRgmNotas.setBounds(65, 15, 120, 25);
		txtRgmNotas.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				buscarDadosParaNotas();
			}
		});
		panelNotas.add(txtRgmNotas);
		
		txtMostraNome = new JTextField();
		txtMostraNome.setEditable(false);
		txtMostraNome.setBackground(new Color(255, 255, 204));
		txtMostraNome.setBorder(new LineBorder(Color.CYAN, 1));
		txtMostraNome.setBounds(200, 15, 410, 25);
		panelNotas.add(txtMostraNome);
		
		txtMostraCurso = new JTextField();
		txtMostraCurso.setEditable(false);
		txtMostraCurso.setBackground(new Color(255, 255, 204));
		txtMostraCurso.setBorder(new LineBorder(Color.CYAN, 1));
		txtMostraCurso.setBounds(20, 50, 590, 25);
		panelNotas.add(txtMostraCurso);
		
		JLabel lblDisc = new JLabel("Disciplina");
		lblDisc.setBounds(20, 90, 60, 25);
		panelNotas.add(lblDisc);
		
		cbDisc = new JComboBox<>(new String[] {"Programação Orientada a Objetos", "Estrutura de Dados I", "Banco de Dados", "Análise de Sistemas"});
		cbDisc.setBounds(90, 90, 520, 25);
		
		cbDisc.addActionListener(e -> {
			if (ignorarEventoTrocaMateria) return;
			
			if (!ultimaDisciplinaSelecionada.isEmpty()) {
				String a1Sel = cbA1.getSelectedItem().toString();
				String a2Sel = cbA2.getSelectedItem().toString();
				String afSel = cbAF.getSelectedItem().toString();
				String fSel = cbFaltas.getSelectedItem().toString();
				
				memoriaNotasTemporarias.put(ultimaDisciplinaSelecionada, new NotasTmp(a1Sel, a2Sel, afSel, fSel));
			}
			
			String novaMateria = cbDisc.getSelectedItem().toString();
			ultimaDisciplinaSelecionada = novaMateria;
			
			if (memoriaNotasTemporarias.containsKey(novaMateria)) {
				NotasTmp dadosSalvos = memoriaNotasTemporarias.get(novaMateria);
				cbA1.setSelectedItem(dadosSalvos.a1);
				cbA2.setSelectedItem(dadosSalvos.a2);
				cbAF.setSelectedItem(dadosSalvos.af);
				cbFaltas.setSelectedItem(dadosSalvos.faltas);
			} else {
				cbA1.setSelectedIndex(0); 
				cbA2.setSelectedIndex(0); 
				cbAF.setSelectedIndex(0); 
				cbFaltas.setSelectedIndex(0);     
			}
		});
		panelNotas.add(cbDisc);
		ultimaDisciplinaSelecionada = cbDisc.getSelectedItem().toString();
		
		JLabel lblSemestre = new JLabel("Semestre");
		lblSemestre.setBounds(20, 130, 60, 25);
		panelNotas.add(lblSemestre);
		
		cbSemestre = new JComboBox<>(new String[] {"2026-1", "2026-2", "2027-1", "2027-2"});
		cbSemestre.setBounds(90, 130, 100, 25);
		panelNotas.add(cbSemestre);
		
		String[] notasRange = {"0.0", "0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};
		
		JLabel lblA1 = new JLabel("A1");
		lblA1.setBounds(210, 130, 25, 25);
		panelNotas.add(lblA1);
		
		cbA1 = new JComboBox<>(notasRange);
		cbA1.setBounds(235, 130, 60, 25);
		panelNotas.add(cbA1);
		
		JLabel lblA2 = new JLabel("A2");
		lblA2.setBounds(310, 130, 25, 25);
		panelNotas.add(lblA2);
		
		cbA2 = new JComboBox<>(notasRange);
		cbA2.setBounds(335, 130, 60, 25);
		panelNotas.add(cbA2);
		
		JLabel lblAF = new JLabel("AF");
		lblAF.setBounds(410, 130, 25, 25);
		panelNotas.add(lblAF);
		
		cbAF = new JComboBox<>(notasRange);
		cbAF.setBounds(435, 130, 60, 25);
		cbAF.setEnabled(false); 
		panelNotas.add(cbAF);
		
		ActionListener verificarHabilitacaoAF = e -> {
			double notaA1 = Double.parseDouble(cbA1.getSelectedItem().toString());
			double notaA2 = Double.parseDouble(cbA2.getSelectedItem().toString());
			if ((notaA1 + notaA2) < 6.0) {
				cbAF.setEnabled(true);
			} else {
				cbAF.setSelectedIndex(0); 
				cbAF.setEnabled(false);
			}
		};
		cbA1.addActionListener(verificarHabilitacaoAF);
		cbA2.addActionListener(verificarHabilitacaoAF);
		
		JLabel lblFaltas = new JLabel("Faltas");
		lblFaltas.setBounds(505, 130, 45, 25);
		panelNotas.add(lblFaltas);
		
		String[] faltasRange = new String[17];
		for (int i = 0; i <= 15; i++) {
			faltasRange[i] = String.valueOf(i);
		}
		faltasRange[16] = ">15";
		
		cbFaltas = new JComboBox<>(faltasRange);
		cbFaltas.setBounds(548, 130, 65, 25);
		panelNotas.add(cbFaltas);
		
		JPanel panelBotoesNotas = new JPanel();
		panelBotoesNotas.setBounds(20, 250, 590, 80);
		panelNotas.add(panelBotoesNotas);
		panelBotoesNotas.setLayout(null);
		
		JButton btnConsultarN = new JButton("<html><center>🔍<br>Consultar</center></html>");
		btnConsultarN.setBounds(10, 10, 85, 60);
		btnConsultarN.addActionListener(e -> buscarDadosParaNotas());
		panelBotoesNotas.add(btnConsultarN);
		
		JButton btnSalvarN = new JButton("<html><center>💾<br>Salvar Tudo</center></html>");
		btnSalvarN.setBounds(110, 10, 100, 60);
		btnSalvarN.addActionListener(e -> salvarTodasAsNotasNoBanco());
		panelBotoesNotas.add(btnSalvarN);
		
		JButton btnExcluirN = new JButton("<html><center>🗑<br>Excluir Matéria</center></html>");
		btnExcluirN.setBounds(230, 10, 110, 60);
		btnExcluirN.addActionListener(e -> excluirNota());
		panelBotoesNotas.add(btnExcluirN);

		
		// Quarta ABA: BOLETIM
		
		JPanel panelBoletim = new JPanel();
		panelBoletim.setLayout(null);
		tabbedPane.addTab("Boletim", null, panelBoletim, null);
		
		JLabel lblInfoBoletim = new JLabel("Boletim de Notas e Faltas Consolidado");
		lblInfoBoletim.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblInfoBoletim.setBounds(20, 15, 350, 20);
		panelBoletim.add(lblInfoBoletim);
		
		String[] colunas = {"Disciplina", "Semestre", "A1", "A2", "AF", "Faltas", "Média Final", "Situação"};
		tableModel = new DefaultTableModel(null, colunas) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		tableBoletim = new JTable(tableModel);
		
		tableBoletim.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				String valStr = (value != null) ? value.toString() : "";
				if ("Aprovado".equals(valStr)) {
					label.setForeground(new Color(0, 150, 0)); 
					label.setFont(label.getFont().deriveFont(Font.BOLD));
				} else if ("DP".equals(valStr) || "DP - Faltas".equals(valStr)) {
					label.setForeground(Color.RED); 
					label.setFont(label.getFont().deriveFont(Font.BOLD));
				} else {
					label.setForeground(Color.BLACK);
				}
				return label;
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(tableBoletim);
		scrollPane.setBounds(20, 50, 595, 250);
		panelBoletim.add(scrollPane);
		
		JButton btnGerarBoletim = new JButton("Atualizar / Emitir Boletim");
		btnGerarBoletim.setBounds(220, 320, 200, 30);
		btnGerarBoletim.addActionListener(e -> carregarBoletim());
		panelBoletim.add(btnGerarBoletim);
	}

	// Caixa de Texto e Alerta institucional do Menu Ajuda no submenu Sobre
	private void exibirPainelSobre() {
		String mensagem = "Para mais informações consulte nossa instituição.\n\n"
				+ "------------------------------------------------------------\n"
				+ "Empresa: Sistemas.DEV\n"
				+ "CNPJ: 12.345.678/0001-99\n"
				+ "Contato: (11) 4002-8922\n"
				+ "E-mail institucional: suporte@sistemas.dev\n"
				+ "Website: www.sistemas.dev.br\n"
				+ "------------------------------------------------------------\n"
				+ "Versão do Software: 2.0.26 - Academic Manager Pro";

		JOptionPane.showMessageDialog(this, mensagem, "Sobre - Sistemas.DEV", JOptionPane.WARNING_MESSAGE);
	}
	
	private boolean validarCamposObrigatorios() {
		String rgmLimpo = txtRgm.getText().replaceAll("\\s", "");
		String dataLimpa = txtData.getText().replaceAll("[^0-9]", "");
		String cpfLimpo = txtCpf.getText().replaceAll("[^0-9]", "");
		String cellLimpo = txtCell.getText().replaceAll("[^0-9]", "");

		if (rgmLimpo.isEmpty() || txtNome.getText().trim().isEmpty() || 
			dataLimpa.isEmpty() || cpfLimpo.isEmpty() || 
			txtEmail.getText().trim().isEmpty() || txtEnd.getText().trim().isEmpty() || 
			txtMunicipio.getText().trim().isEmpty() || cellLimpo.isEmpty()) {
			
			JOptionPane.showMessageDialog(this, "Atenção: Todos os campos da aba 'Dados Pessoais' devem ser preenchidos!");
			return false;
		}
		return true;
	}

	private void salvarAluno() {
		if (!validarCamposObrigatorios()) return;

		String sql = "INSERT INTO aluno VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = obterConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, txtRgm.getText().trim());
			stmt.setString(2, txtNome.getText().trim());
			stmt.setString(3, txtData.getText().trim());
			stmt.setString(4, txtCpf.getText().trim());
			
			String emailCompleto = txtEmail.getText().trim() + "@cs.unicid.edu.br";
			stmt.setString(5, emailCompleto);
			
			stmt.setString(6, txtEnd.getText().trim());
			stmt.setString(7, txtMunicipio.getText().trim());
			stmt.setString(8, cbUf.getSelectedItem().toString());
			stmt.setString(9, txtCell.getText().trim());
			stmt.setString(10, cbCurso.getSelectedItem().toString());
			stmt.setString(11, cbCampus.getSelectedItem().toString());
			
			String periodo = rbMatutino.isSelected() ? "Matutino" : rbVespertino.isSelected() ? "Vespertino" : "Noturno";
			stmt.setString(12, periodo);
			
			stmt.executeUpdate();
			JOptionPane.showMessageDialog(this, "Aluno cadastrado com sucesso!");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Erro ao salvar aluno: " + ex.getMessage());
		}
	}

	private void consultarAluno(String rgmBusca, String origemAba) {
		if(rgmBusca.replaceAll("\\s", "").isEmpty()) {
			JOptionPane.showMessageDialog(this, "Insira um RGM para buscar.");
			return;
		}
		String sql = "SELECT * FROM aluno WHERE rgm = ?";
		try (Connection conn = obterConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, rgmBusca);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				txtRgm.setText(rs.getString("rgm"));
				txtRgmCurso.setText(rs.getString("rgm"));
				
				txtNome.setText(rs.getString("nome"));
				txtData.setText(rs.getString("data_nascimento"));
				txtCpf.setText(rs.getString("cpf"));
				
				String emailBanco = rs.getString("email");
				if(emailBanco != null && emailBanco.contains("@cs.unicid.edu.br")) {
					emailBanco = emailBanco.replace("@cs.unicid.edu.br", "");
				}
				txtEmail.setText(emailBanco);
				
				txtEnd.setText(rs.getString("endereco"));
				txtMunicipio.setText(rs.getString("municipio"));
				cbUf.setSelectedItem(rs.getString("uf"));
				txtCell.setText(rs.getString("celular"));
				
				cbCurso.setSelectedItem(rs.getString("curso"));
				cbCampus.setSelectedItem(rs.getString("campus"));
				
				String p = rs.getString("periodo");
				if ("Matutino".equals(p)) rbMatutino.setSelected(true);
				else if ("Vespertino".equals(p)) rbVespertino.setSelected(true);
				else rbNoturno.setSelected(true);
				
				JOptionPane.showMessageDialog(this, "Aluno localizado!");
			} else {
				if ("curso".equals(origemAba)) {
					JOptionPane.showMessageDialog(this, "Aluno não cadastrado, forneça as informações", "Aviso", JOptionPane.WARNING_MESSAGE);
					cbCurso.setSelectedIndex(0);
					cbCampus.setSelectedIndex(0);
					rbNoturno.setSelected(true);
					txtRgm.setText(rgmBusca);
				} else {
					JOptionPane.showMessageDialog(this, "Aluno não cadastrado no sistema.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void alterarAluno() {
		if (!validarCamposObrigatorios()) return;

		String sql = "UPDATE aluno SET nome=?, data_nascimento=?, cpf=?, email=?, endereco=?, municipio=?, uf=?, celular=?, curso=?, campus=?, periodo=? WHERE rgm=?";
		try (Connection conn = obterConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, txtNome.getText().trim());
			stmt.setString(2, txtData.getText().trim());
			stmt.setString(3, txtCpf.getText().trim());
			
			String emailCompleto = txtEmail.getText().trim() + "@cs.unicid.edu.br";
			stmt.setString(4, emailCompleto);
			
			stmt.setString(5, txtEnd.getText().trim());
			stmt.setString(6, txtMunicipio.getText().trim());
			stmt.setString(7, cbUf.getSelectedItem().toString());
			stmt.setString(8, txtCell.getText().trim());
			stmt.setString(9, cbCurso.getSelectedItem().toString());
			stmt.setString(10, cbCampus.getSelectedItem().toString());
			String periodo = rbMatutino.isSelected() ? "Matutino" : rbVespertino.isSelected() ? "Vespertino" : "Noturno";
			stmt.setString(11, periodo);
			stmt.setString(12, txtRgm.getText().trim());
			
			stmt.executeUpdate();
			JOptionPane.showMessageDialog(this, "Dados do aluno updated com sucesso!");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Erro ao alterar cadastro: " + ex.getMessage());
		}
	}

	private void excluirAluno() {
		String sql = "DELETE FROM aluno WHERE rgm = ?";
		try (Connection conn = obterConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, txtRgm.getText().trim());
			int deletados = stmt.executeUpdate();
			if(deletados > 0) {
				JOptionPane.showMessageDialog(this, "Aluno e dados de tabelas vinculadas removidos.");
			} else {
				JOptionPane.showMessageDialog(this, "Nenhum aluno correspondente para remoção.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void buscarDadosParaNotas() {
		String rgmBusca = txtRgmNotas.getText().trim();
		if (rgmBusca.isEmpty()) return;
		
		String sqlAluno = "SELECT nome, curso FROM aluno WHERE rgm = ?";
		try (Connection conn = obterConexao(); PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno)) {
			stmtAluno.setString(1, rgmBusca);
			ResultSet rsAluno = stmtAluno.executeQuery();
			if (rsAluno.next()) {
				txtMostraNome.setText(rsAluno.getString("nome"));
				txtMostraCurso.setText(rsAluno.getString("curso"));
				
				memoriaNotasTemporarias.clear();
				
				String sqlNotas = "SELECT disciplina, nota_a1, nota_a2, nota_af, faltas FROM notas_faltas WHERE rgm_aluno = ?";
				try (PreparedStatement stmtNotas = conn.prepareStatement(sqlNotas)) {
					stmtNotas.setString(1, rgmBusca);
					ResultSet rsNotas = stmtNotas.executeQuery();
					while(rsNotas.next()) {
						memoriaNotasTemporarias.put(
							rsNotas.getString("disciplina"), 
							new NotasTmp(
								rsNotas.getString("nota_a1"), 
								rsNotas.getString("nota_a2"), 
								rsNotas.getString("nota_af"), 
								rsNotas.getString("faltas") 
							)
						);
					}
				}
				
				ignorarEventoTrocaMateria = true;
				cbDisc.setSelectedIndex(0);
				ultimaDisciplinaSelecionada = cbDisc.getSelectedItem().toString();
				
				if (memoriaNotasTemporarias.containsKey(ultimaDisciplinaSelecionada)) {
					NotasTmp res = memoriaNotasTemporarias.get(ultimaDisciplinaSelecionada);
					cbA1.setSelectedItem(res.a1);
					cbA2.setSelectedItem(res.a2);
					cbAF.setSelectedItem(res.af);
					cbFaltas.setSelectedItem(res.faltas);
				} else {
					cbA1.setSelectedIndex(0);
					cbA2.setSelectedIndex(0);
					cbAF.setSelectedIndex(0);
					cbFaltas.setSelectedIndex(0);
				}
				ignorarEventoTrocaMateria = false;
				
			} else {
				txtMostraNome.setText("RGM INVÁLIDO OU NÃO ENCONTRADO");
				txtMostraCurso.setText("");
				memoriaNotasTemporarias.clear();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void salvarTodasAsNotasNoBanco() {
		String rgmBusca = txtRgmNotas.getText().trim();
		if(txtMostraCurso.getText().isEmpty() || txtMostraNome.getText().contains("INVÁLIDO")) {
			JOptionPane.showMessageDialog(this, "Não é possível salvar. Localize um aluno válido primeiro.");
			return;
		}

		String a1At = cbA1.getSelectedItem().toString();
		String a2At = cbA2.getSelectedItem().toString();
		String afAt = cbAF.getSelectedItem().toString();
		String fAt = cbFaltas.getSelectedItem().toString();
		
		memoriaNotasTemporarias.put(ultimaDisciplinaSelecionada, new NotasTmp(a1At, a2At, afAt, fAt));

		String sql = "INSERT INTO notas_faltas (rgm_aluno, disciplina, semestre, nota_a1, nota_a2, nota_af, faltas) VALUES (?, ?, ?, ?, ?, ?, ?) "
				   + "ON DUPLICATE KEY UPDATE nota_a1=?, nota_a2=?, nota_af=?, faltas=?";
		
		int registrosSalvos = 0;
		String semestreSelecionado = cbSemestre.getSelectedItem().toString();

		try (Connection conn = obterConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (Map.Entry<String, NotasTmp> entrada : memoriaNotasTemporarias.entrySet()) {
				String materia = entrada.getKey();
				NotasTmp dados = entrada.getValue();

				stmt.setString(1, rgmBusca);
				stmt.setString(2, materia);
				stmt.setString(3, semestreSelecionado);
				stmt.setString(4, dados.a1);
				stmt.setString(5, dados.a2);
				stmt.setString(6, dados.af);
				stmt.setString(7, dados.faltas); 
				
				stmt.setString(8, dados.a1);
				stmt.setString(9, dados.a2);
				stmt.setString(10, dados.af);
				stmt.setString(11, dados.faltas);
				
				stmt.executeUpdate();
				registrosSalvos++;
			}
			JOptionPane.showMessageDialog(this, "Sucesso! " + registrosSalvos + " disciplinas foram sincronizadas com o banco.");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Erro ao salvar notas: " + ex.getMessage());
		}
	}

	private void excluirNota() {
		String sql = "DELETE FROM notas_faltas WHERE rgm_aluno = ? AND disciplina = ? AND semestre = ?";
		try (Connection conn = obterConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			String materiaAtual = cbDisc.getSelectedItem().toString();
			stmt.setString(1, txtRgmNotas.getText().trim());
			stmt.setString(2, materiaAtual);
			stmt.setString(3, cbSemestre.getSelectedItem().toString());
			
			stmt.executeUpdate();
			memoriaNotasTemporarias.remove(materiaAtual);
			cbA1.setSelectedIndex(0);
			cbA2.setSelectedIndex(0);
			cbAF.setSelectedIndex(0);
			cbFaltas.setSelectedIndex(0);
			
			JOptionPane.showMessageDialog(this, "Registro da matéria '" + materiaAtual + "' removido.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void carregarBoletim() {
		tableModel.setRowCount(0);
		String rgmBusca = txtRgmNotas.getText().trim();
		
		if (rgmBusca.isEmpty() || txtMostraNome.getText().isEmpty() || txtMostraNome.getText().contains("INVÁLIDO")) {
			JOptionPane.showMessageDialog(this, "Por favor, informe e consulte qual RGM está sendo colocado na aba 'Notas e Faltas' primeiro!", "RGM Não Informado", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String sql = "SELECT disciplina, semestre, nota_a1, nota_a2, nota_af, faltas FROM notas_faltas WHERE rgm_aluno = ? ORDER BY disciplina";
		try (Connection conn = obterConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, rgmBusca);
			ResultSet rs = stmt.executeQuery();
			
			boolean temRegistros = false;
			while(rs.next()) {
				temRegistros = true;
				
				double a1 = rs.getDouble("nota_a1");
				double a2 = rs.getDouble("nota_a2");
				double af = rs.getDouble("nota_af");
				String faltas = rs.getString("faltas");
				
				double somaInicial = a1 + a2;
				double notaFinal = somaInicial;
				
				if (somaInicial < 6.0 && af > 0.0) {
					double menorNota = Math.min(a1, a2);
					if (af > menorNota) {
						notaFinal = (somaInicial - menorNota) + af;
					}
				}
				
				String situacao;
				if (">15".equals(faltas)) {
					situacao = "DP - Faltas"; 
				} else if (notaFinal < 6.0) {
					situacao = "DP"; 
				} else {
					situacao = "Aprovado"; 
				}
				
				tableModel.addRow(new Object[]{
					rs.getString("disciplina"),
					rs.getString("semestre"),
					String.format("%.1f", a1),
					String.format("%.1f", a2),
					String.format("%.1f", af),
					faltas,
					String.format("%.1f", notaFinal),
					situacao
				});
			}
			
			if(!temRegistros) {
				JOptionPane.showMessageDialog(this, "Nenhuma nota localizada no banco de dados para o RGM: " + rgmBusca);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}