var API = "http://localhost:8080/api";

// =========================
// HELPERS
// =========================
function getToken() { return localStorage.getItem("token"); }

function getUsuario() {
    try {
        var u = localStorage.getItem("usuarioLogado");
        return u ? JSON.parse(u) : null;
    } catch(e) {
        return null;
    }
}

function authHeaders() {
    return { "Content-Type": "application/json", "Authorization": "Bearer " + getToken() };
}

// =========================
// LOGIN
// =========================
function login() {
    var usuario = document.getElementById("loginUsuario").value.trim();
    var senha   = document.getElementById("loginSenha").value.trim();
    if (!usuario || !senha) { alert("Preencha todos os campos!"); return; }

    fetch(API + "/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ usuario: usuario, senha: senha })
    })
    .then(async res => {
        if (!res.ok) throw new Error("Usuário ou senha inválidos!");
        return res.json();
    })
    .then(data => {
        localStorage.setItem("token", data.token);
        localStorage.setItem("usuarioLogado", JSON.stringify({
            id: data.id, nome: data.nome, usuario: data.usuario, tipo: data.tipo
        }));
        var tipo = (data.tipo || "").toUpperCase();
        if (tipo === "PROFESSOR")    window.location.href = "professor.html";
        else if (tipo === "ADMIN")   window.location.href = "admin.html";
        else if (tipo === "RESPONSAVEL") window.location.href = "responsavel.html";
        else                         window.location.href = "aluno.html";
    })
    .catch(err => alert("❌ " + err.message));
}

// =========================
// LOGOUT
// =========================
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuarioLogado");
    window.location.href = "index.html";
}

// =========================
// MOSTRAR USUÁRIO
// =========================
function mostrarUsuario() {
    var u = getUsuario();
    if (!u) return;
    var el = document.getElementById("usuarioLogado");
    var tp = document.getElementById("tipoUsuario");
    if (el) el.innerText = u.nome;
    if (tp) tp.innerText = u.tipo;
}

// =========================
// MOSTRAR SEÇÃO
// =========================
function mostrarSecao(secao) {
    document.querySelectorAll("section").forEach(s => s.classList.remove("active"));
    document.querySelectorAll(".sidebar nav li").forEach(l => l.classList.remove("active"));

    var alvo = document.getElementById(secao);
    if (alvo) alvo.classList.add("active");

    var nav = document.querySelector(`[data-secao="${secao}"]`);
    if (nav) nav.classList.add("active");

    if (secao === "turmas")       carregarTurmas();
    if (secao === "disciplinas")  carregarDisciplinas();
    if (secao === "alunos")       carregarAlunos();
    if (secao === "professores")  carregarProfessores();
    if (secao === "notas")        carregarNotas();
    if (secao === "frequencias")  carregarFrequencias();
    if (secao === "usuarios")     carregarUsuarios();
    if (secao === "responsaveis") carregarResponsaveis();

    if (secao === "lancarnotas")  carregarSelectsParaLancamento();
    if (secao === "dashboard" && window.location.pathname.includes("responsavel.html")) carregarDadosFilhos();
}

// =========================
// CADASTRO DE USUÁRIOS
// =========================
function salvarUsuario(usuario) {
    fetch(API + "/cadastro", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(usuario)
    })
    .then(async res => {
        if (!res.ok) {
            var err = await res.text();
            throw new Error(err.includes("já existe") ? "Usuário já cadastrado!" : "Erro ao cadastrar!");
        }
        return res.json();
    })
    .then(() => { alert("✅ Cadastro realizado!"); window.location.href = "index.html"; })
    .catch(err => alert("❌ " + err.message));
}

function cadastrarProfessor() {
    var nome = document.getElementById("nome").value.trim();
    var usuario = document.getElementById("usuario").value.trim();
    var senha = document.getElementById("senha").value.trim();
    var materia = document.getElementById("materia").value.trim();
    var registro = document.getElementById("registro").value.trim();
    if (!nome || !usuario || !senha || !materia) { alert("Preencha os campos obrigatórios!"); return; }
    salvarUsuario({ nome: nome, usuario: usuario, senha: senha, tipo: "PROFESSOR", materia: materia, registro: registro });
}

function cadastrarAluno() {
    var nome = document.getElementById("nome").value.trim();
    var usuario = document.getElementById("usuario").value.trim();
    var senha = document.getElementById("senha").value.trim();
    var matricula = document.getElementById("matricula").value.trim();
    if (!nome || !usuario || !senha) { alert("Preencha os campos obrigatórios!"); return; }
    salvarUsuario({ nome: nome, usuario: usuario, senha: senha, tipo: "ALUNO", matricula: matricula });
}

function adicionarCampoFilho() {
    var container = document.getElementById("filhos-container");
    var count = container.querySelectorAll('.matricula-input').length + 1;
    var input = document.createElement("input");
    input.type = "text";
    input.className = "matricula-input";
    input.placeholder = "Matrícula do Aluno " + count;
    container.appendChild(input);
}

function cadastrarResponsavel() {
    var nome = document.getElementById("nome").value.trim();
    var rg = document.getElementById("rg").value.trim();
    var telefone = document.getElementById("telefone").value.trim();
    var usuario = document.getElementById("usuario").value.trim();
    var senha = document.getElementById("senha").value.trim();

    var matriculasAlunos = Array.from(document.querySelectorAll('.matricula-input'))
                                  .map(i => i.value.trim())
                                  .filter(v => v !== "");

    if (!nome || !rg || !usuario || !senha || matriculasAlunos.length === 0) {
        alert("Preencha os campos obrigatórios e informe pelo menos uma matrícula!");
        return;
    }

    salvarUsuario({
        nome: nome, rg: rg, telefone: telefone, usuario: usuario, senha: senha,
        tipo: "RESPONSAVEL", matriculasAlunos: matriculasAlunos
    });
}

// =========================
// CADASTRO DE TURMAS E DISCIPLINAS
// =========================
function criarTurma() {
    var nome    = document.getElementById("turmaNome").value.trim();
    var periodo = document.getElementById("turmaPeriodo").value;

    if (!nome || !periodo) { alert("Preencha o nome e selecione o período!"); return; }

    fetch(API + "/turmas", {
        method: "POST", headers: authHeaders(),
        body: JSON.stringify({ nome: nome, ano: "", periodo: periodo })
    })
    .then(r => r.json())
    .then(() => {
        document.getElementById("turmaMensagem").innerText = "✅ Turma criada!";
        carregarTurmas();
        document.getElementById("turmaNome").value = "";
        document.getElementById("turmaPeriodo").value = "";
    }).catch(() => { document.getElementById("turmaMensagem").innerText = "❌ Erro ao criar turma."; });
}

function criarDisciplina() {
    var nome = document.getElementById("discNome").value.trim();

    if (!nome) { alert("Informe o nome da disciplina!"); return; }

    var prefixo = nome.normalize("NFD").replace(/[\u0300-\u036f]/g, "").substring(0, 3).toUpperCase();
    var numero = Math.floor(Math.random() * 900) + 100;
    var codigo = prefixo + numero;

    fetch(API + "/disciplinas", {
        method: "POST", headers: authHeaders(),
        body: JSON.stringify({ nome: nome, codigo: codigo })
    })
    .then(r => r.json())
    .then(() => {
        document.getElementById("discMensagem").innerText = `✅ Disciplina criada com o código: ${codigo}`;
        carregarDisciplinas();
        document.getElementById("discNome").value = "";
    }).catch(() => { document.getElementById("discMensagem").innerText = "❌ Erro ao criar disciplina."; });
}

// =========================
// LANÇAMENTO DE NOTAS
// =========================
function carregarSelectsParaLancamento() {
    fetch(API + "/alunos", { headers: authHeaders() })
    .then(res => res.json())
    .then(alunos => {
        var selectAluno = document.getElementById("notaAlunoId");
        if (selectAluno) {
            selectAluno.innerHTML = '<option value="">Selecione o Aluno...</option>' +
                alunos.map(a => `<option value="${a.id}">${a.nome} (Matrícula: ${a.matricula || '-'})</option>`).join("");
        }
    }).catch(() => {});

    fetch(API + "/disciplinas", { headers: authHeaders() })
    .then(res => res.json())
    .then(disciplinas => {
        var selectDisc = document.getElementById("notaDisciplinaId");
        if (selectDisc) {
            selectDisc.innerHTML = '<option value="">Selecione a Disciplina...</option>' +
                disciplinas.map(d => `<option value="${d.id}">${d.nome} (${d.codigo})</option>`).join("");
        }
    }).catch(() => {});
}

function lancarNota() {
    var alunoId = document.getElementById("notaAlunoId").value;
    var disciplinaId = document.getElementById("notaDisciplinaId").value;
    var bimestre = document.getElementById("notaBimestre").value;
    var valor = document.getElementById("notaValor").value;

    if (!alunoId || !disciplinaId || !bimestre || !valor) {
        alert("Por favor, preencha todos os campos para lançar a nota!");
        return;
    }

    var payload = {
        alunoId: parseInt(alunoId),
        disciplinaId: parseInt(disciplinaId),
        bimestre: parseInt(bimestre),
        valor: parseFloat(valor)
    };

    fetch(API + "/notas", {
        method: "POST",
        headers: authHeaders(),
        body: JSON.stringify(payload)
    })
    .then(res => {
        if (!res.ok) throw new Error("Erro ao salvar nota");
        return res.json();
    })
    .then(() => {
        document.getElementById("notaMensagem").innerText = "✅ Nota lançada com sucesso no sistema!";
        document.getElementById("notaValor").value = "";
    })
    .catch(() => {
        document.getElementById("notaMensagem").innerText = "❌ Ocorreu um erro ao salvar a nota. Tente novamente.";
    });
}

// =========================
// CARREGAR DADOS
// =========================
function carregarTurmas() {
    fetch(API + "/turmas", { headers: authHeaders() })
    .then(res => res.json())
    .then(turmas => {
        var lista = document.getElementById("listaTurmas");
        if (!lista) return;
        if (!turmas.length) { lista.innerHTML = '<li class="empty-state"><span>🏫</span><p>Nenhuma turma cadastrada</p></li>'; return; }

        lista.innerHTML = turmas.map(t => `
            <li>🏫 <strong>${t.nome}</strong> <span style="margin-left:auto;" class="badge badge-blue">${t.periodo}</span></li>
        `).join("");
    }).catch(() => {});
}

function carregarDisciplinas() {
    fetch(API + "/disciplinas", { headers: authHeaders() })
    .then(res => res.json())
    .then(disciplinas => {
        var lista = document.getElementById("listaDisciplinas");
        if (!lista) return;
        if (!disciplinas.length) { lista.innerHTML = '<li>Nenhuma disciplina cadastrada</li>'; return; }
        lista.innerHTML = disciplinas.map(d => `
            <li>📚 <strong>${d.nome}</strong> <span class="badge badge-blue">${d.codigo || ""}</span></li>
        `).join("");
    }).catch(() => {});
}

function carregarAlunos() {
    fetch(API + "/alunos", { headers: authHeaders() })
    .then(res => res.json())
    .then(alunos => {
        var lista = document.getElementById("listaAlunos");
        if (!lista) return;
        if (!alunos.length) { lista.innerHTML = '<li>Nenhum aluno cadastrado</li>'; return; }
        lista.innerHTML = alunos.map(a => `
            <li>🎓 <strong>${a.nome}</strong> — Matrícula: ${a.matricula || "—"}</li>
        `).join("");
    }).catch(() => {});
}

function carregarProfessores() {
    fetch(API + "/professores", { headers: authHeaders() })
    .then(res => res.json())
    .then(professores => {
        var lista = document.getElementById("listaProfessores");
        if (!lista) return;
        if (!professores.length) { lista.innerHTML = '<li>Nenhum professor cadastrado</li>'; return; }
        lista.innerHTML = professores.map(p => `
            <li>👨‍🏫 <strong>${p.nome}</strong> — ${p.materia || "—"}</li>
        `).join("");
    }).catch(() => {});
}

function carregarUsuarios() {
    fetch(API + "/usuarios", { headers: authHeaders() })
    .then(res => res.json())
    .then(usuarios => {
        var lista = document.getElementById("listaUsuarios");
        if (!lista) return;
        lista.innerHTML = usuarios.map(u => `
            <tr>
                <td>${u.nome}</td>
                <td>${u.usuario}</td>
                <td><span class="badge badge-blue">${u.tipo}</span></td>
            </tr>
        `).join("");
    }).catch(() => {});
}

function carregarResponsaveis() {
    fetch(API + "/responsaveis", { headers: authHeaders() })
    .then(res => res.json())
    .then(responsaveis => {
        var lista = document.getElementById("listaResponsaveis");
        if (!lista) return;
        if (!responsaveis.length) {
            lista.innerHTML = '<li>Nenhum responsável cadastrado</li>';
            return;
        }

        lista.innerHTML = responsaveis.map(r => {
            var nomesFilhos = r.alunos && r.alunos.length > 0
                ? r.alunos.map(a => a.nome).join(", ")
                : "Nenhum aluno vinculado";

            return `
                <li>
                    👨‍👧 <strong>${r.nome}</strong>
                    <span style="font-size:13px; color:#666; margin-left: 10px;">
                        | RG: ${r.rg || "—"} | Tel: ${r.telefone || "—"}
                    </span>
                    <br>
                    <small style="display:block; margin-top:4px; color:#1e3a5f;">
                        <strong>Filhos:</strong> ${nomesFilhos}
                    </small>
                </li>
            `;
        }).join("");
    }).catch(() => {});
}

function carregarNotas() {
    var u = getUsuario();
    if (!u || !u.id) return;
    fetch(API + "/notas/aluno/" + u.id, { headers: authHeaders() })
    .then(res => res.json())
    .then(notas => {
        var lista = document.getElementById("listaNotas");
        if (!lista) return;
        if (!notas.length) { lista.innerHTML = '<div class="empty-state"><span>📝</span><p>Nenhuma nota lançada ainda</p></div>'; return; }

        lista.innerHTML = notas.map(n => {
            var v = n.valor || 0;
            var cls = v >= 7 ? "badge badge-blue" : v >= 5 ? "badge badge-yellow" : "badge";
            var nomeMateria = n.disciplina && n.disciplina.nome ? n.disciplina.nome : "Matéria";
            return `
            <div style="padding: 12px; border: 1px solid #ddd; margin-bottom: 8px; border-radius: 8px; display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <strong>${nomeMateria}</strong>
                    <span style="color:#666; font-size: 13px; margin-left: 8px;">${n.bimestre ? n.bimestre + "º Bimestre" : ""}</span>
                </div>
                <div class="${cls}" style="font-size: 16px; font-weight: bold; padding: 6px 12px;">
                    ${v.toFixed(1)}
                </div>
            </div>`;
        }).join("");
    }).catch(() => {});
}

function carregarFrequencias() {
    var u = getUsuario();
    if (!u || !u.id) return;
    fetch(API + "/frequencias/aluno/" + u.id, { headers: authHeaders() })
    .then(res => res.json())
    .then(freqs => {
        var lista = document.getElementById("listaFrequencias");
        if (!lista) return;
        if (!freqs.length) { lista.innerHTML = '<li class="empty-state">Nenhum registro de frequência</li>'; return; }
        lista.innerHTML = freqs.map(f => `
            <li>
                ${f.presente ? "✅" : "❌"}
                <strong>${f.disciplina?.nome || "—"}</strong>
                — ${f.data}
                ${f.observacao ? '<span class="badge badge-yellow">' + f.observacao + '</span>' : ""}
            </li>
        `).join("");
    }).catch(() => {});
}

// =========================
// MÁGICA DO RESPONSÁVEL (NOVO)
// =========================
function carregarDadosFilhos() {
    var u = getUsuario();
    if (!u || !u.id) return;

    fetch(API + "/responsaveis", { headers: authHeaders() })
    .then(res => res.json())
    .then(responsaveis => {
        // Encontra quem é o pai logado na lista
        var meuPerfil = responsaveis.find(r => r.id === u.id || r.usuario === u.usuario);
        var container = document.getElementById("listaFilhosDashboard");

        if (!meuPerfil || !meuPerfil.alunos || meuPerfil.alunos.length === 0) {
            if(container) container.innerHTML = '<div class="section-card"><p>Nenhum aluno vinculado ao seu perfil no momento.</p></div>';
            return;
        }

        // Desenha a estrutura (cartão) para cada filho
        if (container) {
            container.innerHTML = meuPerfil.alunos.map(aluno => `
                <div class="section-card" style="margin-bottom: 20px;">
                    <h2 style="border-bottom: 1px solid #ddd; padding-bottom: 8px; margin-bottom: 12px;">
                        🎓 ${aluno.nome} <span style="font-size:14px; font-weight:normal; color:#666;">(Matrícula: ${aluno.matricula || '-'})</span>
                    </h2>
                    <div id="notas-filho-${aluno.id}">
                        <p style="color: #666; font-size: 14px;">Carregando notas do aluno...</p>
                    </div>
                </div>
            `).join("");

            // Dispara a busca de notas para CADA filho e injeta dentro do cartão dele
            meuPerfil.alunos.forEach(aluno => {
                fetch(API + "/notas/aluno/" + aluno.id, { headers: authHeaders() })
                .then(res => res.json())
                .then(notas => {
                    var divNotas = document.getElementById("notas-filho-" + aluno.id);
                    if (!notas.length) {
                        divNotas.innerHTML = "<p>Nenhuma nota lançada para este aluno ainda.</p>";
                        return;
                    }

                    divNotas.innerHTML = notas.map(n => {
                        var v = n.valor || 0;
                        var cls = v >= 7 ? "badge badge-blue" : v >= 5 ? "badge badge-yellow" : "badge";
                        var nomeMateria = n.disciplina && n.disciplina.nome ? n.disciplina.nome : "Matéria";
                        return `
                        <div style="padding: 10px; border: 1px solid #eee; margin-bottom: 6px; border-radius: 6px; display: flex; justify-content: space-between; align-items: center; background: #fafafa;">
                            <div>
                                <strong>${nomeMateria}</strong>
                                <span style="color:#666; font-size: 13px; margin-left: 8px;">${n.bimestre ? n.bimestre + "º Bimestre" : ""}</span>
                            </div>
                            <div class="${cls}" style="font-size: 15px; font-weight: bold; padding: 4px 10px;">
                                ${v.toFixed(1)}
                            </div>
                        </div>`;
                    }).join("");
                }).catch(() => {
                    document.getElementById("notas-filho-" + aluno.id).innerHTML = "<p>Erro ao carregar as notas.</p>";
                });
            });
        }
    }).catch(() => {});
}