
const content = document.querySelector("#contenido");
const resultados = document.querySelector('#resultados');
const datalist = document.querySelector('#allpokemons');
const searchBtn = document.querySelector('.search_btn');
const search_input = document.querySelector('.search_input');
const logo = document.querySelector('.logo');
const filtersBtn = document.querySelector('#filter');
const filters = document.querySelector('.filters');
const tipo_filter = document.querySelector('#tipo_filter');
const gen_filter = document.querySelector('#gen_filter');
const filter_reset = document.querySelector('.filter_reset');
const filter_filter = document.querySelector('.filter_filter');
const go_up = document.querySelector('.floating_btn');
const mediaM = window.matchMedia('(min-width:768px)');


const back = document.querySelector('#back');
const MAX_BASE = 255;



var pokemons = [];
var pokemons_loaded = 0;
var loading = false;
var results = false;
var first_content_load = true;

var info_type_filter_loaded = [];


/*
  
Colores:
    rojo: #EA5455
    blanco: #F6F6F6
    amarillo: #FFB400
    azul: #2D4059

Fuentes:
    Nunito
    Open Sans
*/

class Pokemon{
    constructor(id,name,url){
        this.name=name;
        this.url=url;
        this.complete_info=false;
        this.type=[];
        this.gen="";
    }
    
    async loadAllInfo(){
        var responsej;
        try {
            var responsef = await fetch(this.url); // https://pokeapi.co/api/v2/pokemon/630/ -> bug
            if (responsef.ok) {
                responsej = await responsef.json();
            } else {
                throw Error("Error Al Intentar rescatar la información del pokemon " + this.id +" :(");
            }

            if(this.name==' '){
                this.name = responsej['name'];
            }
            this.id=responsej['id']
            this.photo=responsej["sprites"]["other"]["official-artwork"]["front_default"];
            if (this.photo==null) {
                this.photo=responsej["sprites"]["front_default"]; 
            }
            var tthis=this;
            this.type=[];
            responsej["types"].map(
                function (x) {
                    tthis.type.push(x["type"]["name"]);
                }
            )
            this.weight= responsej['weight']/10;
            this.height= responsej['height']/10;
            this.be = responsej['base_experience'];
            var arrStats = responsej['stats'];
            var stats = new Map();
            for(i=0; i<arrStats.length; i++){
                var stat = arrStats[i];
                stats.set(stat['stat']['name'], stat['base_stat']);
            }
           
            this.stats=Array.from(stats.entries());
            var urlSpecie=responsej["species"]["url"];
            responsef = await fetch(urlSpecie);
            if(responsef.ok){
                responsej = await responsef.json();
            } else {
                throw Error("Error Al Intentar rescatar la información del pokemon " + this.id +" :(");
            }
            var text_flavor = responsej['flavor_text_entries'];
            var len = text_flavor.length;
            for(var i=0; text_flavor[i]['language']['name']!='en' && i<len; i++);
            this.text= text_flavor[i]['flavor_text'];
            this.complete_info=true;

        } catch (error) {
            setError(error.message);
        }
    }
  
    getPokemonItem(){
        var pokemonItem_innerHTML;
        const pokemonItem = document.createElement("div")
        pokemonItem.setAttribute('onkeypress', `changeResultsScreen(${this.id})`);
        pokemonItem.setAttribute('onclick', `changeResultsScreen(${this.id})`);
        pokemonItem.setAttribute('role', `listitem`);
        pokemonItem.setAttribute('tabindex', '0');
        pokemonItem.setAttribute('aria-haspopup', 'true');
        pokemonItem.classList.add("pokemonitem");
        if (this.photo!=null) 
            pokemonItem_innerHTML=`
                <img class="poke_img" alt="Image of ${this.name}" src="${this.photo}">
            `;
        else
            pokemonItem_innerHTML=`
                <span class="no_poke_img" role=”img”>Foto no Disponible :(</span>
            `;
        pokemonItem_innerHTML += `    
            <span class="poke_id">#${this.id}</span>
            <span class="poke_name">${this.name[0].toUpperCase() + this.name.slice(1)}</span>   
        `;
        this.type.forEach(element => {
            pokemonItem_innerHTML += `    
                <span class="poke_type type_${element}">${element}</span>
            `;
        });
        pokemonItem.innerHTML = pokemonItem_innerHTML;
        return pokemonItem;   
    }

    getPokemonResult(){
        //Foto
        var result_image;
        if(this.photo!=null){
            result_image = document.createElement('img');
            result_image.setAttribute('class', 'result_image');
            result_image.setAttribute('src', this.photo);
            result_image.setAttribute('alt', this.name + " Image");
        } else {
            result_image = document.createElement('div');
            result_image.setAttribute('class', 'result_image');
            result_image.innerHTML='<span class="result_no_image_text">Foto no Disponible :(</span>'
        }
        result_image.setAttribute('tabindex', '0');
        resultados.appendChild(result_image);

        //Nombre
        var name = document.createElement('span');
        name.setAttribute('class', 'name_result');
        name.innerHTML=this.name[0].toUpperCase() + this.name.slice(1);
        name.setAttribute('tabindex', '0');
        resultados.appendChild(name);
       
        //Id
        var id = document.createElement('span');
        id.setAttribute('class', 'id_result');
        id.innerHTML="#" + this.id;
        id.setAttribute('tabindex', '0');
        resultados.appendChild(id);

        //Fact
        var divDesc = document.createElement('div');
        divDesc.setAttribute('class', 'descrpt_result');
    
        divDesc.innerHTML = 
        `
            <span class='descpt_title_result' id="fact_title">Fact</span>
            <p class='descpt_text_result' aria-describedby="fact_title">${cleanJson(this.text).trim()}</p>
        `;
        divDesc.setAttribute('tabindex', '0');
        resultados.appendChild(divDesc);
        
        //Peso Altura y Base XP
        var info = document.createElement('div');
        info.setAttribute('class', 'info_hwb_result');
    
        info.innerHTML = `
            <table class='tInfo'>
                <caption class="hide">Height, Weight and Base EXP</caption>
                <tr class='rowInfo'>
                    <th class ='headInfo'>Height</th>
                    <th class ='headInfo'>Weight</th>
                    <th class ='headInfo'>Base EXP</th>
                </tr>
                <tr class='rowInfo'>
                    <td class ='dInfo'>${this.height} m</td>
                    <td class ='dInfo'>${this.weight} kg</td>
                    <td class ='dInfo'>${this.be}</td>
                </tr>
            </table>
        `;
        info.setAttribute('tabindex', '0');
        resultados.appendChild(info);
      
        //Tipos
        var div_t = document.createElement('div');
        div_t.setAttribute('class', 'type_result');
        div_t.innerHTML = 
        `
            <span class='type_title_result' id="types_title">Types</span>
        `;

        this.type.forEach(element => {
            div_t.innerHTML += `    
                <span class="types_result type_${element}" aria-describedby="types_title">${element.toUpperCase()}</span>
            `;}
        )
        div_t.setAttribute('tabindex', '0');
        resultados.appendChild(div_t);
        
        //Stats
        var div_bs = document.createElement('div');
        div_bs.setAttribute('class', 'stats_result');
        div_bs.innerHTML = 
        `
            <span class='stats_title_result' id="stats">Base Stats</span>
        `;
        var table = document.createElement('table');
        table.setAttribute("aria-describedby","stats")
        var stats = this.stats;
        for(var i=0; i<stats.length; i++){
            var key = stats[i][0];
            var value = stats[i][1];
            var per = value/MAX_BASE*100;
            table.innerHTML += 
            `
                <tr> 
                    <td class='stat_result'>${key.toUpperCase()}</td>
                    <td>
                        <div class='stat_pro_ext_result' role="progressbar" aria-valuenow="${value}" aria-valuemin="0" aria-valuemax="255">
                            <div class='stat_pro_int_result' style='width:${per}%'>
                                <label class='stat_value_result'>${value}</label>
                            </div>
                        </div>
                    </td>
                </tr>
            `;
        }
        div_bs.appendChild(table);
        div_bs.setAttribute('tabindex', '0');
        resultados.appendChild(div_bs);
    }
}

//Esto lo usamos debido a que la api introduce caracteres extraños
function cleanJson(s) {
    return (s
        .replace(/\u000c/gi, ' ')
    );
}

//Cargar Información del Pokemon
async function chargeResults(id){
    results=true;
    content.classList.toggle('hide');
    content.setAttribute("aria-hidden","true");
    resultados.classList.toggle('hide');
    resultados.setAttribute("aria-hidden","false");
    filtersBtn.classList.toggle('hide');
    filtersBtn.setAttribute("aria-hidden","true");
    searchBtn.classList.toggle('hide_left_btn');
    searchBtn.setAttribute("aria-hidden","true");
    back.classList.toggle('hide');
    back.setAttribute("aria-hidden","false");
    go_up.classList.toggle('hide');
    go_up.setAttribute("aria-hidden","true");
    
    var poke;
    //por si se accede directamente al recurso
    if(id>pokemons.length){
        poke = new Pokemon(id, ' ', 'https://pokeapi.co/api/v2/pokemon/'+id);
    }else {
        poke = pokemons[id-1];
    }
    if(!poke.complete_info){
        await poke.loadAllInfo();
    }

    poke.getPokemonResult()
    document.title = "Pokemon " + poke.name;

}

//Cargar Lista Principal
function chargeContent(){
    results=false;
    first_content_load=false;
    fetchPokeList();
    fetchPokeTypes();
    fetchPokeGens();
    document.title = "PokeApi";
    probeMedia(mediaM);
}

//Comprobamos si tenemos parámetros get y consecuentemente 
//determinamos si se debe mostrar la parte de resultados o la lista
function isResults(href){
    var param = href.split('?',2);
    if(param.length>1){
        var id = param[1].split('=',2);
        if(id.length>1 && id[0]=='id'){
            return id[1];
        }
    }
    return -1;
}

//Si la url hace referencia a un pokemon lo cargamos
function evaluateUrl(){
    var id = isResults(document.location.href);
    if(id==-1)
        chargeContent();
    else 
        chargeResults(id);
}

//De lista a resultados
function changeResultsScreen(id){
    results=true;
    var href = document.location.href + '?' + 'id=' + id;
    hasLoad();
    window.history.pushState({}, 'Results', href);
    evaluateUrl();
}

function changeContentScreen(){
    results=false;
    content.classList.toggle('hide');
    content.setAttribute("aria-hidden","false");
    resultados.classList.toggle('hide');
    resultados.setAttribute("aria-hidden","true");
    resultados.innerHTML = '';
    filtersBtn.classList.toggle('hide');
    filtersBtn.setAttribute("aria-hidden","false");
    back.classList.toggle('hide');
    back.setAttribute("aria-hidden","true");
    searchBtn.classList.toggle('hide_left_btn');
    searchBtn.setAttribute("aria-hidden","false");
    go_up.classList.toggle('hide');
    go_up.setAttribute("aria-hidden","false");
    document.title = "PokeApi";
}

//Para que se pueda volver hacia atrás desde los botones del navegador cuando se visualiza un pokemon
function hasLoad(){
    window.onpopstate = function (){
        changeContentScreen();
    }  
}


//Lista de todos pokemons 
//La usaremos para el autocompletado y para cargar todos los pokemons por pantalla
function fetchPokeList(){
    fetch("https://pokeapi.co/api/v2/pokemon/?limit=-1").then(
        function(response) {
            if (response.ok) {
                return response.json();
            } else {
                
                throw Error("Error Al Intentar rescatar la lista de pokemons :(");
            }
        }
    ).then(
        function(response) {
            pokemon_list = "";
            for (let index = 0; index < response['results'].length; index++) {
                //Añadimos el pokemon como opción para poder buscarlo mas sencillamente
                pokemon_list = pokemon_list + `<option value="${response['results'][index]["name"]}"/>`;
                //Añadimos el pokemon a nuestra colección
                pokemons.push(new Pokemon(index+1, response['results'][index]["name"],response['results'][index]["url"].slice(0,-1)));
            };
            datalist.innerHTML = pokemon_list;
            loadmorePokemon(false); //async
        }
        
    ).catch(
        function (error) {
            setError(error.message);
        }
    );
}

//Cargar los tipos de pokemon
function fetchPokeTypes(){
    fetch("https://pokeapi.co/api/v2/type/").then(
        function(response) {
            if (response.ok) {
                return response.json();
            } else {
                throw Error("Error Al Intentar rescatar las tipos de pokemons :(");
            }
        }
    ).then(
        function(response) {
            pokemon_type_list = "<option selected>Cualquiera</option>";
            for (let index = 0; index < response['results'].length; index++) {
                //Añadimos el pokemon como opción para poder buscarlo mas sencillamente
                pokemon_type_list = pokemon_type_list + `<option>${response['results'][index]["name"]}</option>`;
                
            };
            tipo_filter.innerHTML = pokemon_type_list;
        }
    ).catch(
        function (error) {
            setError(error.message);
        }
    );

}
    

//Cargar las generaciones de pokemon
function fetchPokeGens(){
    fetch("https://pokeapi.co/api/v2/generation/").then(
        function(response) {
            if (response.ok) {
                return response.json();
            } else {
                throw Error("Error Al Intentar rescatar las generaciones de pokemons :(");
            }
        }
    ).then(
        function(response) {
            pokemon_gen_list = "<option selected>Cualquiera</option>";
            for (let index = 0; index < response['results'].length; index++) {
                //Añadimos el pokemon como opción para poder buscarlo mas sencillamente
                pokemon_gen_list = pokemon_gen_list + `<option>${response['results'][index]["name"]}</option>`;
                
            };
            gen_filter.innerHTML = pokemon_gen_list;
        }
    ).catch(
        function (error) {
            setError(error.message);
        }
    );

}


//Aquí esta puesta la constante que nos dice cuantos poquemons cargar
async function loadmorePokemon(new_search) {
    const pokemon_filtered=pokemons.filter(filter);
    if (pokemon_filtered.length>0) {
        content.innerHTML+='<div class="cargando"><img src="pokeball.png" alt="cargando..." class="poke_spinner"></div>'
        const loads=40;
        if (new_search) {
            pokemons_loaded=0;
        }
        let index = pokemons_loaded;
        var children = [];

        console.log("cargando pokemons " + pokemons_loaded + " al " + (pokemons_loaded + loads) + " de " + pokemon_filtered.length);

        while (index < pokemons_loaded + loads && index < pokemon_filtered.length) {
            if (!pokemon_filtered[index].complete_info) 
                await pokemon_filtered[index].loadAllInfo();
            children.push(pokemon_filtered[index].getPokemonItem())
            index++
        }
        pokemons_loaded += loads;
        content.removeChild(content.lastChild);
        children.map((e) => {content.appendChild(e)});
        loading=false; //para que no se estén cargando simultáneamente
    } else
        content.innerHTML = `
            <span class="error">No se ha encontrado nungún pokemon :(</span>
        `;
}

//Filtros
function filter(pokemon) {
    var apto=true;
    if (tipo_filter.value!="Cualquiera") {
        apto = apto && pokemon.type.includes(tipo_filter.value);
    }
    if (gen_filter.value!="Cualquiera") {
        apto = apto && pokemon.gen==gen_filter.value;
    }
    if (search_input.value!="") {
        apto = apto && pokemon.name.includes(search_input.value.toLowerCase());
    }
    return apto;
}

//Tenemos que recuperar la información de la api dado que sin ella, recorrer los 
//1000 pokemons y hacer 1 petición por cada uno en el peor caso sería demasiado lento
async function get_filter_info(endpoint,element) {
    var responsej;
    try {
        const responsef = await fetch("https://pokeapi.co/api/v2/" + endpoint + "/" + element);
        if (responsef.ok) {
            responsej = await responsef.json();
        } else {
            throw Error("Error Al Intentar la información de los filtros :(");
        }    
    } catch (error) {
        setError(error.message);
    }
    //Añadimos esta información a los pokemons
    switch (endpoint) {
        case "type":
            for (let index = 0; index < responsej['pokemon'].length; index++) {
                pokemons.map( (pokemon) => {
                    if ((pokemon.complete_info==false)&&pokemon.name==responsej['pokemon'][index]["pokemon"]["name"]) {
                        pokemon.type.push(element);
                    }
                });
            }
            break;
    
        case "generation":
            for (let index = 0; index < responsej['pokemon_species'].length; index++) {
                pokemons.filter((pokemon) => {
                    return pokemon.name==responsej['pokemon_species'][index]["name"];
                }).map((x) => {x.gen=element})
            }
            break;

        default:
            break;
    }
}

function setError(info) {
    content.innerHTML=`
        <span class="error">${info}</span>
    `;
}

searchBtn.onclick = function(){
    logo.classList.toggle('hide_search');
    if (logo.getAttribute("aria-hidden")=="true")
        logo.setAttribute("aria-hidden","false");
    else
        logo.setAttribute("aria-hidden","true");
    search_input.classList.toggle('hide_search');
    if (search_input.getAttribute("aria-hidden")=="true")
        search_input.setAttribute("aria-hidden","false");
    else
        search_input.setAttribute("aria-hidden","true");

};

filtersBtn.onclick = function(){
    filters.classList.toggle('filters_shown');
    if (filters.getAttribute("aria-expanded")=="true")
        filters.setAttribute("aria-expanded","false");
    else
        filters.setAttribute("aria-expanded","true");
};

go_up.onclick = function(){
    content.scrollTo({top: 0,behavior: "smooth"});

};

back.onclick = function(){
    window.history.back();
};

search_input.addEventListener('change', _ => {
    if(!results){
        content.innerHTML = ''; //con esto borramos todos los items
        //de todas formas, si está bien programado "hace que imprima todos los poquemos que de nombre macheen"
        loadmorePokemon(true)
    } else{
        window.history.pushState({}, '', "/");
        changeContentScreen();
        if(first_content_load){
            chargeContent();
        }
        else{
            content.innerHTML = ''; 
            loadmorePokemon(true)
        }
    }
});

//Necesitaremos cargar informacion necesaria para el filtrado
//loading se usa para que no de problemas de concurrencia
filter_filter.onclick = async _ => {
    if (!loading) {
        loading=true;
        if (tipo_filter.value!="Cualquiera") 
            await get_filter_info("type",tipo_filter.value);
        if (gen_filter.value!="Cualquiera")
            await get_filter_info("generation",gen_filter.value);
        content.innerHTML = ''; 
        loadmorePokemon(true)   
    }
};

//Limpiamos Filtrado
filter_reset.onclick = async _ => {
    if (!loading) {
        loading=true;
        tipo_filter.value = "Cualquiera";
        gen_filter.value = "Cualquiera";
        content.innerHTML = ''; 
        loadmorePokemon(true)   
    }
};

//Detectamos cuando se baja de todo para hacer el scroll infinito.
content.addEventListener('scroll', function() {
    if (content.scrollTop + content.clientHeight >= content.scrollHeight) {
        if (!loading) {
            loading=true;
            loadmorePokemon(false);
        }
    }
  });

//Arreglamos los aria-hiddens que no se pueden poner con css
mediaM.addEventListener('change', probeMedia);

function probeMedia(media){
    if(media.matches){
        searchBtn.setAttribute('aria-hidden', 'true');
        search_input.setAttribute('aria-hidden', 'false');
    }
    else {
        if(!results)
            searchBtn.setAttribute('aria-hidden', 'false');
        search_input.setAttribute('aria-hidden', 'true');
    }
}

//Evaluamos el url y en función de esto, cargamos todo
evaluateUrl();
