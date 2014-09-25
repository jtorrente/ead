package es.eucm.ead.engine.repo;

import es.eucm.ead.editor.demobuilder.RepoLibraryBuilder;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.schema.editor.components.repo.RepoLicense;

/**
 * Created by Javier Torrente on 25/09/14.
 */
public class MockupIconsLib extends RepoLibraryBuilder{
    public MockupIconsLib() {
        super("mockup_icons");
    }

    private static String[] es = {
            "Galería del sistema;Android",
            "Volver;Atrás,Flecha",
            "Parpadear",
            "Cámara;foto",
            "Copiar;clonar,duplicar",
            "Conversación;hablar,mensaje",
            "Reducir;redimensionar,empequeñecer,decrementar",
            "Efectos de imagen",
            "Borrador;goma",
            "Primera;inicial",
            "Entrar en puerta",
            "Salir de puerta",
            "Home;casa",
            "Mover horizontal",
            "Aumentar;redimensionar,agrandar,aumentar",
            "Información;info",
            "Interactivo;touch,click",
            "Cargando 1;loading",
            "Cargando 2;Loading",
            "Candado;bloqueado",
            "Menú de aplicación",
            "Nueva escena;añadir escena",
            "Otros",
            "Pintar (coloreado)",
            "Pintar (blanco y negro)",
            "Pegar",
            "Dibujar;lápiz",
            "Jugar (naranja y negro);Play",
            "Jugar (naranja);Play",
            "Papelera;borrar,eliminar",
            "Rehacer;Flecha",
            "Borrar;eliminar",
            "Ordenar;A-Z,reordenar",
            "Repositorio;descargar",
            "Rotar;flecha",
            "Borrar;borrador",
            "Buscar",
            "Configuración;propiedades",
            "Compartir",
            "Sonido",
            "Texto",
            "Al fondo",
            "Al frente",
            "Animar;animación",
            "Deshacer;flecha",
            "Variable",
            "Mover vertical",
            "Visibilidad;ojo"};


    private static String[] en = {
            "System Gallery;Android",
            "Back;Arrow",
            "Blink",
            "Camera;photo",
            "Copy;clone,duplicate",
            "Conversation;talk,speak,message",
            "Decrease;resize",
            "Image effects",
            "Eraser",
            "First",
            "Entering gateway",
            "Exiting gateway",
            "Home",
            "Move Horizontally",
            "Increase;resize",
            "Info",
            "Interactive;touch,click",
            "Loading 1",
            "Loading 2",
            "Lock",
            "App menu",
            "New scene;add scene",
            "Others",
            "Paint (colored)",
            "Paint (black and white)",
            "Paste",
            "Draw;pencil",
            "Play (orange and black)",
            "Play (orange)",
            "Recycle bin;delete,remove",
            "Redo;arrow",
            "Remove;delete",
            "Sort;A-Z,reorder",
            "Repository;download",
            "Rotate;arrow",
            "Rubber;eraser",
            "Search",
            "Settings",
            "Share",
            "Sound",
            "Text",
            "To back",
            "To front",
            "Animate;animation",
            "Undo;arrow",
            "Variable",
            "Move vertically",
            "Visibility;eye"};

    private static String[] files = {"android_gallery80x80.png",
            "back80x80.png",
            "blink80x80.png",
            "camera80x80.png",
            "clone80x80.png",
            "conversation.png",
            "decrease80x80.png",
            "effects80x80.png",
            "eraser.png",
            "first.png",
            "gateway.png",
            "gatewayreverse.png",
            "home.png",
            "horizontalmove80x80.png",
            "increase80x80.png",
            "info.png",
            "interactive.png",
            "loading1.png",
            "loading2.png",
            "lock.png",
            "menu.png",
            "new_scene.png",
            "others.png",
            "paint.png",
            "paint80x80.png",
            "paste.png",
            "pencil.png",
            "play.png",
            "play_orange.png",
            "recycle.png",
            "redo.png",
            "remove80x80.png",
            "reorder.png",
            "repository80x80.png",
            "rotate80x80.png",
            "rubber.png",
            "search.png",
            "settings.png",
            "share.png",
            "sound.png",
            "text.png",
            "toback80x80.png",
            "tofront80x80.png",
            "tween80x80.png",
            "undo.png",
            "variable.png",
            "verticalmove80x80.png",
            "visibility.png"};


    @Override
    protected void doBuild() {
        setCommonProperty(THUMBNAILS, "res_and_thum/");
        setCommonProperty(RESOURCES, "res_and_thum/");

        setCommonProperty(MAX_WIDTH, "80");
        setCommonProperty(MAX_HEIGHT, "80");
        setCommonProperty(TAGS, "eAdventure,eUCM,mockup,icon;icono");
        setCommonProperty(AUTHOR_NAME, "Antonio Calvo & Cristian Rotaru");
        setCommonProperty(LICENSE, RepoLicense.CC_BY.toString());

        for (int i=0; i<files.length; i++){
            String nameEn = en[i].split(";")[0];
            String tagsEn[] = en[i].split(";").length>1?en[i].split(";")[1].split(","):new String[0];
            String nameEs = es[i].split(";")[0];
            String tagsEs[] = es[i].split(";").length>1?es[i].split(";")[1].split(","):new String[0];

            /*if (i==2 || i==9 || i==15|| i==20|| i==23 || i== 27 || i==41 || i==42 || i==43){
                System.out.println(files[i]);
                continue;
            }*/
            repoEntity(nameEn, nameEs, "", "", files[i], files[i]);
            for (String tagEn: tagsEn){
                tag(tagEn, "");
            }
            for (String tagEs: tagsEs){
                tag("", tagEs);
            }
        }

        repoLib("Icons from Game Mockup Editor app", "Iconos de Mockup", "", "", null);
    }

    public static void main (String[]args){
        MockupIconsLib lib = new MockupIconsLib();
        lib.export("D:\\Documents\\TRABAJO\\E-ADVENTURE\\Mockup\\Repo\\");
    }
}
