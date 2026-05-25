#!/usr/bin/env bash
# RNF-08 — Teste automatizado de backup e recuperação de dados.
# Cria um dump da BD de testes, elimina um registo, restaura e verifica que reaparece.
# Requer: make db-reset-test já executado 
set -euo pipefail

DB_NAME="${TEST_DB_NAME:-hotelanimais_test}"
DB_USER="${DB_USERNAME:-hoteluser}"
DB_PASS="${DB_PASSWORD:-hotelpass}"
CONTAINER="hotelanimais-db-tests"
DUMP_FILE="$(mktemp /tmp/backup-test-XXXXXX.sql)"
TEST_USER="backup_probe_user"

cleanup() { rm -f "$DUMP_FILE"; }
trap cleanup EXIT

run_sql() {
    docker exec -i "$CONTAINER" \
        mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" --batch --skip-column-names -e "$1" 2>/dev/null
}

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  RNF-08 — Teste de Backup e Recuperação"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

echo "→ [1/5] Inserir registo de prova..."
run_sql "INSERT IGNORE INTO colaborador(username,nome,email,password_hash,tipo_colaborador,ativo)
         VALUES('$TEST_USER','Backup Test','bt@test.local','\$2a\$10\$xx','CUIDADOR',TRUE);"

COUNT_BEFORE=$(run_sql "SELECT COUNT(*) FROM colaborador WHERE username='$TEST_USER';")
if [ "$COUNT_BEFORE" -ne "1" ]; then
    echo "✗ Falha ao inserir registo de prova." && exit 1
fi
echo "   ✓ Registo inserido."

echo "→ [2/5] Criar dump (mysqldump)..."
docker exec "$CONTAINER" \
    mysqldump -u"$DB_USER" -p"$DB_PASS" --single-transaction --quick "$DB_NAME" \
    2>/dev/null > "$DUMP_FILE"
echo "   ✓ Dump criado: $DUMP_FILE ($(wc -l < "$DUMP_FILE") linhas)"

echo "→ [3/5] Eliminar registo de prova da BD..."
run_sql "DELETE FROM colaborador WHERE username='$TEST_USER';"
COUNT_DELETED=$(run_sql "SELECT COUNT(*) FROM colaborador WHERE username='$TEST_USER';")
if [ "$COUNT_DELETED" -ne "0" ]; then
    echo "✗ Falha ao eliminar registo." && exit 1
fi
echo "   ✓ Registo eliminado."

echo "→ [4/5] Restaurar backup..."
docker exec -i "$CONTAINER" \
    mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" 2>/dev/null < "$DUMP_FILE"
echo "   ✓ Backup restaurado."

echo "→ [5/5] Verificar integridade após restauro..."
COUNT_AFTER=$(run_sql "SELECT COUNT(*) FROM colaborador WHERE username='$TEST_USER';")
if [ "$COUNT_AFTER" -ne "1" ]; then
    echo ""
    echo "✗ FALHA: registo não foi restaurado (encontrado $COUNT_AFTER, esperado 1)."
    exit 1
fi

# Limpar registo de prova
run_sql "DELETE FROM colaborador WHERE username='$TEST_USER';"

echo "   ✓ Registo presente após restauro."
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  ✓ RNF-08 PASSOU — backup e recuperação funcionam."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
