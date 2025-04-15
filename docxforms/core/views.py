import io
from django import forms
from django.http import HttpResponse
from django.shortcuts import render
from docxtpl import DocxTemplate

class UploadDocxForm(forms.Form):
    arquivo = forms.FileField(label="Selecione um arquivo .docx")

def home_view(request):
    if request.method == 'POST':
        form = UploadDocxForm(request.POST, request.FILES)
        if form.is_valid():
            docx_file = request.FILES['arquivo']

            template = DocxTemplate(docx_file)

            contexto = {
                'nome': 'João da Silva',
                'horario_chegada': '15:00',
                'horario_saida': '15:25'
            }

            template.render(contexto)

            # Salva em memória para enviar como resposta
            buffer = io.BytesIO()
            template.save(buffer)
            buffer.seek(0)

            response = HttpResponse(buffer.read(), content_type='application/vnd.openxmlformats-officedocument.wordprocessingml.document')
            response['Content-Disposition'] = 'attachment; filename=documento_preenchido.docx'
            return response
    else:
        form = UploadDocxForm()

    return render(request, 'upload_docx.html', {'form': form})