package br.com.jv.todolist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/primeiraRota")
//http://localhost:8080/-----
public class MinhaPrimeiraController {
    
    // Metodos de acesso do HTTP
    //get - Buscar uma informação
    //post - Adicionar um dado/informação
    //put - Alterar um dado/informação
    //delete - Remover um dado
    //patch - Alterar somente uma parte da info/dado

    //Metodo (Funcionalidade) de uma classe
    @GetMapping("/primeiroMetodo")
    public String primeiraMensagem(){
        return "Funcionou";
    }
}
