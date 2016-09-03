package com.fafeltech.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import org.w3c.dom.css.Rect;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaro;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture canoBaixo2;
	private Texture canoTopo2;
	private Random numeroRandomico;
	private Random numeroRandomico2;
	private BitmapFont fonte;
	private Circle passaroCirculo ;
	private Rectangle canoTopoRetangulo ;
	private Rectangle canoBaixoRetangulo ;
	private Rectangle canoTopoRetangulo2;
	private Rectangle canoBaixoRetangulo2;
//	private ShapeRenderer shape ;
	private Texture gameOver ;
	private BitmapFont mensagem ;
	private BitmapFont recorde;
	private BitmapFont msgRecorde;
	private BitmapFont creditos;

	//Atributos de configuração do jogo
//	private int larguraDispositivo;
//	private int alturaDispositivo;
	private int estadoJogo = 0 ;  // jogo recebe 3 "status", onde 0 é jogo não iniciado e 1 é jogo iniciado, 2 é game over
	private int pontuacao = 0 ; // marca pontuação na tela
	private int recordeTela; //marcaRecorde - começa com zero

	private float variacao = 0;
	private float velocidadeQueda = 0 ;
	private float posicaoInicialVertical ;
	private float posicaoMovimentoCanoHorizontal ;
	private float posicaoMovimentoCanoHorizontal2 ;
	private float espacoEntreCanos ;
	private float deltaTime ;
	private float alturaEntreCanosRandomica;
	private float alturaEntreCanosRandomica2;

	private boolean marcaPonto ; //por padrão, variável "boolean" (que é "true" ou "false), vem "false"

	//para ajustar resolução
	private OrthographicCamera camera ;
	private Viewport viewport ;
	private final float VIRTUAL_WIDTH = 768 ;
	private final float VIRTUAL_HEIGHT = 1024 ;
	private float larguraDispositivo;
	private float alturaDispositivo;

	@Override
	public void create () {
		//método "create" é executado somente uma vez

		batch = new SpriteBatch();
		numeroRandomico = new Random();
		numeroRandomico2 = new Random();

		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(4);

		recorde = new BitmapFont();
		recorde.setColor(Color.GOLD);
		recorde.getData().setScale(2);

		passaro = new Texture[3]; //nos colchetes se coloca o número de elementos
		passaro[0] = new Texture("passaro1.png");
		passaro[1] = new Texture("passaro2.png");
		passaro[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		canoBaixo2 = new Texture("cano_baixo2.png");
		canoTopo2 = new Texture("cano_topo2.png");
		gameOver = new Texture("game_over.png");

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		msgRecorde = new BitmapFont();
		msgRecorde.setColor(Color.GOLD);
		msgRecorde.getData().setScale(2);

		creditos = new BitmapFont();
		creditos.setColor(Color.FIREBRICK);
		creditos.getData().setScale(2);

		passaroCirculo = new Circle();
		canoTopoRetangulo = new Rectangle();
		canoBaixoRetangulo = new Rectangle();
		canoBaixoRetangulo2 = new Rectangle();
		canoTopoRetangulo2 = new Rectangle();
//		shape = new ShapeRenderer();

		//Configurações da câmera
		camera = new OrthographicCamera();
		camera.position.set( VIRTUAL_WIDTH /2 , VIRTUAL_HEIGHT / 2 , 0 );
		viewport = new StretchViewport ( VIRTUAL_WIDTH , VIRTUAL_HEIGHT , camera );

		// larguraDispositivo = Gdx.graphics.getWidth(); - esse comando for for usar a do dispositivo. no jogo se usará um tamanho virtual
		// alturaDispositivo = Gdx.graphics.getHeight();
		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVertical = alturaDispositivo / 2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo - 100;
		posicaoMovimentoCanoHorizontal2 = posicaoMovimentoCanoHorizontal + larguraDispositivo / 2 ;
		espacoEntreCanos = 320;

	}

	@Override
	public void render () {
		Preferences setRecorde = Gdx.app.getPreferences("Preferencia");
		recordeTela = setRecorde.getInteger("Recorde");

		camera.update();

		//limpar frames de execuções anteriores para otimizar app
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

		deltaTime = Gdx.graphics.getDeltaTime();
		//variacao += 0.08; // o "+=" quer dizer que soma o mesmo valor da variação
		variacao += deltaTime * 4;
		//para voltar ao início da Array passaro
		if (variacao > 2) {
			variacao = 0;
		}


		if ( estadoJogo == 0 ) {//se jogo não está iniciado

			if ( Gdx.input.justTouched()  ) { //se tela é tocada e jogo deve ser iniciado
					estadoJogo = 1;
			}

		}else { //o "else tá valendo até lá embaixo antes do batch, para iniciar tudo somente se estadoJogo = 1, ou seja, jogo iniciado - 2 será game over

			//aqui iniciou o jogo

			//incremento da velocidade queda
			velocidadeQueda++;
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
				posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
			}

			if ( estadoJogo == 1) {
				//decrementa posição do cano - cano se mexe na tela
				posicaoMovimentoCanoHorizontal -= deltaTime * 200;
				posicaoMovimentoCanoHorizontal2 -= deltaTime * 200;

				// o justTouched retorna "true" quando se toca na tela
				if (Gdx.input.justTouched()) {
					velocidadeQueda = -20;
				}

				//verifica se cano saiu inteiramente da tela
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200; //subtrai metade para atingir numeros negativos e positivos - o Random vai gerar numeros entre 0 e 400
					marcaPonto = false;
				}

				if (posicaoMovimentoCanoHorizontal2 < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal2 = larguraDispositivo;
					alturaEntreCanosRandomica2 = numeroRandomico2.nextInt(400) - 200; //subtrai metade para atingir numeros negativos e positivos - o Random vai gerar numeros entre 0 e 400
					marcaPonto = false;
				}

				//verifica pontuação - cada vez que canos passarem pelo passaro - incrementa a pontuação.
				if ( posicaoMovimentoCanoHorizontal < 120 || posicaoMovimentoCanoHorizontal2 < 120 ) {

					if ( !marcaPonto ) {  // a exclamação ( ! ) é um marcador de negação - se não for false, nesse caso
						pontuacao++;
						marcaPonto = true;
					}

				}

				if ( pontuacao >= recordeTela ) { //salvar recorde na tela
						recordeTela = pontuacao;
					setRecorde.putInteger("Recorde", recordeTela);
					setRecorde.flush();
				}

			}else { //aqui vai rolar a tela de game over, pois não será mais estadoJogo == 1, se mudar, o passaro para, os canos não mexem, e tudo o que é executado no 1 pára

					if ( Gdx.input.justTouched() ) {

						estadoJogo = 0 ;
						pontuacao = 0;
						velocidadeQueda = 0 ;
						posicaoInicialVertical = alturaDispositivo / 2 ;
						posicaoMovimentoCanoHorizontal = larguraDispositivo ;
						posicaoMovimentoCanoHorizontal2 = posicaoMovimentoCanoHorizontal + larguraDispositivo / 2 + canoTopo2.getWidth() ;

					}
			}

		}

		//Configurar dados de projeção da câmera
		batch.setProjectionMatrix( camera.combined );

		//inicia a renderização
		batch.begin();

		//os desenhos, o que é colocado primeiro vai para o fundo
		//desenha na tela e a posição (x,y) = (horizontal, vertical)

		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
		batch.draw( canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica );
		batch.draw ( canoBaixo , posicaoMovimentoCanoHorizontal , alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica );
		batch.draw( canoTopo2, posicaoMovimentoCanoHorizontal2, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica2 );
		batch.draw ( canoBaixo2 , posicaoMovimentoCanoHorizontal2 , alturaDispositivo / 2 - canoBaixo2.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica2 );
		batch.draw(passaro[ (int) variacao ], 120, posicaoInicialVertical);
		fonte.draw(batch , String.valueOf(pontuacao) , larguraDispositivo / 2 , alturaDispositivo - 50 );
		msgRecorde.draw(batch, String.valueOf("Recorde") , 50 , alturaDispositivo - 50);
		recorde.draw(batch, String.valueOf(recordeTela), 50 , alturaDispositivo - 100) ;
		creditos.draw(batch, "by Ivécio Filho", 50 , 50);

		if ( estadoJogo == 2 ) {
			batch.draw(gameOver, larguraDispositivo /2 - gameOver.getWidth() / 2 , alturaDispositivo / 2 );
			mensagem.draw(batch , "Toque para Reiniciar!" , larguraDispositivo /2 - 200, alturaDispositivo / 2 - gameOver.getHeight() /2 );
		}

		//finaliza a renderização
		batch.end();

		passaroCirculo.set(120 + passaro[0].getWidth() / 2 , posicaoInicialVertical + passaro[0].getHeight() /2 , passaro[0].getWidth() / 2 ); //tem que ver bem as medidas para ficar em cima do passaro
		canoBaixoRetangulo.set(
				posicaoMovimentoCanoHorizontal , // eixo X
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica , //eixo Y
				canoBaixo.getWidth(),
				canoBaixo.getHeight()
		);
		canoTopoRetangulo.set(
				posicaoMovimentoCanoHorizontal , // eixo X
				alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica , //eixo Y
				canoTopo.getWidth(),
				canoTopo.getHeight()
		);
		canoBaixoRetangulo2.set(
				posicaoMovimentoCanoHorizontal2 , // eixo X
				alturaDispositivo / 2 - canoBaixo2.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica2 , //eixo Y
				canoBaixo2.getWidth(),
				canoBaixo2.getHeight()
		);
		canoTopoRetangulo2.set(
				posicaoMovimentoCanoHorizontal2 , // eixo X
				alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica2 , //eixo Y
				canoTopo2.getWidth(),
				canoTopo2.getHeight()
		);

		//desenhar as formas - porque as texturas não colidem, tem que se criar formas
		/*shape.begin( ShapeRenderer.ShapeType.Filled ); //Filled quer dizer que as formas já são preenchidas

		shape.circle( passaroCirculo.x, passaroCirculo.y , passaroCirculo.radius ); //posições do passaroCirculo, poderia ser direto também - 3 paramentros - x , y , raio do circulo
		shape.rect(canoBaixoRetangulo.x, canoBaixoRetangulo.y , canoBaixoRetangulo.width , canoBaixoRetangulo.height );
		shape.rect( canoTopoRetangulo.x, canoTopoRetangulo.y , canoTopoRetangulo.width , canoTopoRetangulo.height );
		shape.setColor( Color.RED );

		shape.end();*/
		// shape só foi usado para desenhar na tel as formas, mas não é usado, pois as forma ficam invisíveis


		//teste de colisão
		if ( Intersector.overlaps( passaroCirculo , canoBaixoRetangulo ) || Intersector.overlaps( passaroCirculo , canoTopoRetangulo) || Intersector.overlaps( passaroCirculo , canoBaixoRetangulo2 ) || Intersector.overlaps( passaroCirculo , canoTopoRetangulo2 )
				|| posicaoInicialVertical <= 0  ||  posicaoInicialVertical >= alturaDispositivo
				) {
			estadoJogo = 2; //para ocorrer game over, muda o estadoJogo

		}

	}

	//esse método que faz a adaptação na tela
	@Override
	public void resize (int width , int height) {
		viewport.update(width , height);
	}

}


