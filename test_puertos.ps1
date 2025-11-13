# Script para probar diferentes puertos del servidor de correo
# Sistema Trans Comarapa - Diagnostico de Puertos

$servidor = "mail.tecnoweb.org.bo"
$puertos = @(25, 143, 465, 587, 993, 995, 110)

Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "PRUEBA DE PUERTOS - mail.tecnoweb.org.bo" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

foreach ($puerto in $puertos) {
    Write-Host "Probando puerto $puerto..." -NoNewline
    
    $resultado = Test-NetConnection -ComputerName $servidor -Port $puerto -WarningAction SilentlyContinue
    
    if ($resultado.TcpTestSucceeded) {
        Write-Host " ABIERTO OK" -ForegroundColor Green
        Write-Host "  >> Este puerto esta accesible" -ForegroundColor Green
    } else {
        Write-Host " CERRADO X" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "LEYENDA DE PUERTOS:" -ForegroundColor Yellow
Write-Host "  25  - SMTP (sin cifrado)" -ForegroundColor White
Write-Host "  110 - POP3 (sin cifrado)" -ForegroundColor White
Write-Host "  143 - IMAP (sin cifrado)" -ForegroundColor White
Write-Host "  465 - SMTP con SSL/TLS" -ForegroundColor White
Write-Host "  587 - SMTP con STARTTLS" -ForegroundColor White
Write-Host "  993 - IMAP con SSL/TLS" -ForegroundColor White
Write-Host "  995 - POP3 con SSL/TLS" -ForegroundColor White
Write-Host "=======================================" -ForegroundColor Cyan

