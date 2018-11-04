package multithread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Coinmonitor extends Application {

	// einmal für die normalen Kurswerte und einmal für die Min/Max Werte
	// darunter
	private TableView<Marktplatz> table = new TableView<>();
	private TableView<Marktplatz> table2 = new TableView<>();

	// Auswahlbox für meine Währung(Monero/Ethereum) im mining calculator
	ComboBox<String> currenciebox = new ComboBox<>();
	// ComboBox<String> hardwarebox = new ComboBox<>();

	// siehe Miningformel für den mining calculator
	double monerodifficulty = 0;
	double moneroreward = 0;
	double ethereumdifficulty = 0;
	double ethereumreward = 0;

	// aktueller Dollarkurs und südkoreanischer won kurs
	double eurodollar = 0;
	double eurowon = 0;

	// URLs für Bezug der Währungskurse sowie Difficulty+Reward -> Liste lohnt
	// sich hier noch nicht
	URL urlhundert = null;
	URL urlhunderteins = null;
	URL urlhundertzwei = null;
	URL urlhundertzehn = null;
	URL urlhundertelf = null;

	int counter = 0;

	// Listen für meine URL Abfragen der Marktplätze sowie die Beträge die aus
	// diesen Abfragen übergeben werden
	ArrayList<URL> urllistanycoin = new ArrayList<URL>();
	ArrayList<URL> urllistbittrex = new ArrayList<URL>();
	ArrayList<URL> urllistpoloniex = new ArrayList<URL>();
	ArrayList<URL> urllisthitbtc = new ArrayList<URL>();
	ArrayList<URL> urllistbithumb = new ArrayList<URL>();
	ArrayList<URL> urllistkraken = new ArrayList<URL>();
	ArrayList<Double> betraege = new ArrayList<Double>();
	ArrayList<Double> betraegebittrex = new ArrayList<Double>();
	ArrayList<Double> betraegepoloniex = new ArrayList<Double>();
	ArrayList<Double> betraegehitbtc = new ArrayList<Double>();
	ArrayList<Double> betraegebithumb = new ArrayList<Double>();
	ArrayList<Double> betraegekraken = new ArrayList<Double>();
	// JSON-String beinhaltet je nach abgefragter Währung ein individuelles
	// Kürzel
	// um diesen Vorgang auch in einer Schleife einzubinden muss bei kraken das
	// passende Kürzel
	// herangezogen werden um aus den JSON-String die benötigten Daten zu ziehen
	ArrayList<String> krakencodes = new ArrayList<String>();

	String result = null;

	// Runtime.getRuntime().availableProcessors() -> coole Methode die mir die
	// maximal verfügbare Anzahl an Kernen liefert
	ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	Callable<String> task1 = () -> {
		this.getValueAnyCoin();
		return "Hello from Callable";
	};

	Callable<String> task2 = () -> {
		this.getValueBittrex();
		return "Hello from Callable";
	};

	Callable<String> task3 = () -> {
		this.getValuePoloniex();
		return "Hello from Callable";
	};

	Callable<String> task4 = () -> {
		this.getValueHitbtc();
		return "Hello from Callable";
	};

	Callable<String> task5 = () -> {
		this.getValueBithumb();
		return "Hello from Callable";
	};

	Callable<String> task6 = () -> {
		this.getValueKraken();
		return "Hello from Callable";
	};

	Callable<String> task7 = () -> {
		this.getMoneroDifficulty();
		return "Hello from Callable";
	};

	Callable<String> task8 = () -> {
		this.getEthereumDifficulty();
		return "Hello from Callable";
	};

	Callable<String> task9 = () -> {
		this.getEthereumReward();
		return "Hello from Callable";
	};

	Callable<String> task10 = () -> {
		this.getDollarKurs();
		return "Hello from Callable";
	};

	Callable<String> task11 = () -> {
		this.getWonKurs();
		return "Hello from Callable";
	};

	Future<String> future1;
	Future<String> future2;
	Future<String> future3;
	Future<String> future4;
	Future<String> future5;
	Future<String> future6;
	Future<String> future7;
	Future<String> future8;
	Future<String> future9;
	Future<String> future10;
	Future<String> future11;

	// füllen der Listen mit den passenden URLs
	public void getURLs() {
		try
		{
			// für Bitcoin
			urllistanycoin.add(new URL("https", "anycoindirect.eu",
					"/api/public/buyprices?CoinCode=BTC&FiatCode=EUR&CoinAmount=1"));

			// für Ethereum
			urllistanycoin.add(new URL("https", "anycoindirect.eu",
					"/api/public/buyprices?CoinCode=ETH&FiatCode=EUR&CoinAmount=1"));

			// für Litecoin
			urllistanycoin.add(new URL("https", "anycoindirect.eu",
					"/api/public/buyprices?CoinCode=LTC&FiatCode=EUR&CoinAmount=1"));

			// für Monero
			urllistanycoin.add(new URL("https", "anycoindirect.eu",
					"/api/public/buyprices?CoinCode=XMR&FiatCode=EUR&CoinAmount=1"));

			// für Dash
			urllistanycoin.add(new URL("https", "anycoindirect.eu",
					"/api/public/buyprices?CoinCode=DASH&FiatCode=EUR&CoinAmount=1"));

			// Bittrex Bitcoin günstigster Preis
			urllistbittrex
					.add(new URL("https", "bittrex.com", "/api/v1.1/public/getorderbook?market=USDT-BTC&type=sell"));

			// Bittrex Ethereum günstigster Preis
			urllistbittrex
					.add(new URL("https", "bittrex.com", "/api/v1.1/public/getorderbook?market=USDT-ETH&type=sell"));

			// Bittrex Ripple günstigster Preis
			urllistbittrex
					.add(new URL("https", "bittrex.com", "/api/v1.1/public/getorderbook?market=USDT-XRP&type=sell"));

			// Bittrex Bitcoin Cash günstigster Preis
			urllistbittrex
					.add(new URL("https", "bittrex.com", "/api/v1.1/public/getorderbook?market=USDT-BCH&type=sell"));

			// Bittrex Litecoin günstigster Preis
			urllistbittrex
					.add(new URL("https", "bittrex.com", "/api/v1.1/public/getorderbook?market=USDT-LTC&type=sell"));

			// Bittrex Monero günstigster Preis
			urllistbittrex
					.add(new URL("https", "bittrex.com", "/api/v1.1/public/getorderbook?market=USDT-XMR&type=sell"));

			// Bittrex Dash günstigster Preis
			urllistbittrex
					.add(new URL("https", "bittrex.com", "/api/v1.1/public/getorderbook?market=USDT-DASH&type=sell"));

			// Poloniex Bitcoin günstigster Preis
			urllistpoloniex.add(
					new URL("https", "poloniex.com", "/public?command=returnOrderBook&currencyPair=USDT_BTC&depth=10"));

			// Poloniex Ethereum günstigster Preis
			urllistpoloniex.add(
					new URL("https", "poloniex.com", "/public?command=returnOrderBook&currencyPair=USDT_ETH&depth=10"));

			// Poloniex Ripple günstigster Preis
			urllistpoloniex.add(
					new URL("https", "poloniex.com", "/public?command=returnOrderBook&currencyPair=USDT_XRP&depth=10"));

			// Poloniex Bitcoin Cash günstigster Preis
			urllistpoloniex.add(
					new URL("https", "poloniex.com", "/public?command=returnOrderBook&currencyPair=USDT_BCH&depth=10"));

			// Poloniex Litecoin günstigster Preis
			urllistpoloniex.add(
					new URL("https", "poloniex.com", "/public?command=returnOrderBook&currencyPair=USDT_LTC&depth=10"));

			// Poloniex Monero günstigster Preis
			urllistpoloniex.add(
					new URL("https", "poloniex.com", "/public?command=returnOrderBook&currencyPair=USDT_XMR&depth=10"));

			// Poloniex Dash günstigster Preis
			urllistpoloniex.add(new URL("https", "poloniex.com",
					"/public?command=returnOrderBook&currencyPair=USDT_DASH&depth=10"));

			// hitbtc Bitcoin günstigster Preis
			urllisthitbtc.add(new URL("https", "api.hitbtc.com", "/api/2/public/orderbook/BTCUSD"));

			// hitbtc Ethereum günstigster Preis
			urllisthitbtc.add(new URL("https", "api.hitbtc.com", "/api/2/public/orderbook/ETHUSD"));

			// hitbtc Ripple günstigster Preis
			urllisthitbtc.add(new URL("https", "api.hitbtc.com", "/api/2/public/orderbook/XRPUSDT"));

			// hitbtc Bitcoin Cash günstigster Preis
			urllisthitbtc.add(new URL("https", "api.hitbtc.com", "/api/2/public/orderbook/BCHUSD"));

			// hitbtc Litecoin günstigster Preis
			urllisthitbtc.add(new URL("https", "api.hitbtc.com", "/api/2/public/orderbook/LTCUSD"));

			// hitbtc Monero günstigster Preis
			urllisthitbtc.add(new URL("https", "api.hitbtc.com", "/api/2/public/orderbook/XMRUSD"));

			// hitbtc Dash günstigster Preis
			urllisthitbtc.add(new URL("https", "api.hitbtc.com", "/api/2/public/orderbook/DASHUSD"));

			// Bithumb Bitcoin günstigster Preis
			urllistbithumb.add(new URL("https", "api.bithumb.com", "/public/orderbook/BTC"));

			// Bithumb Ethereum günstigster Preis
			urllistbithumb.add(new URL("https", "api.bithumb.com", "/public/orderbook/ETH"));

			// Bithumb Ripple günstigster Preis
			urllistbithumb.add(new URL("https", "api.bithumb.com", "/public/orderbook/XRP"));

			// Bithumb Bitcoin Cash günstigster Preis
			urllistbithumb.add(new URL("https", "api.bithumb.com", "/public/orderbook/BCH"));

			// Bithumb Litecoin günstigster Preis
			urllistbithumb.add(new URL("https", "api.bithumb.com", "/public/orderbook/LTC"));

			// Bithumb Monero günstigster Preis
			urllistbithumb.add(new URL("https", "api.bithumb.com", "/public/orderbook/XMR"));

			// Bithumb Dash günstigster Preis
			urllistbithumb.add(new URL("https", "api.bithumb.com", "/public/orderbook/DASH"));

			// Kraken Bitcoin günstigster Preis
			urllistkraken.add(new URL("https", "api.kraken.com", "/0/public/Depth?pair=XBTEUR"));

			// Kraken Ethereum günstigster Preis
			urllistkraken.add(new URL("https", "api.kraken.com", "/0/public/Depth?pair=ETHEUR"));

			// Kraken Ripple günstigster Preis
			urllistkraken.add(new URL("https", "api.kraken.com", "/0/public/Depth?pair=XRPEUR"));

			// Kraken Bitcoin Cash günstigster Preis
			urllistkraken.add(new URL("https", "api.kraken.com", "/0/public/Depth?pair=BCHEUR"));

			// Kraken Litecoin günstigster Preis
			urllistkraken.add(new URL("https", "api.kraken.com", "/0/public/Depth?pair=LTCEUR"));

			// Kraken Monero günstigster Preis
			urllistkraken.add(new URL("https", "api.kraken.com", "/0/public/Depth?pair=XMREUR"));

			// Kraken Dash günstigster Preis
			urllistkraken.add(new URL("https", "api.kraken.com", "/0/public/Depth?pair=DASHEUR"));

			// für Difficulty und Block Reward Monero
			URL url100 = new URL("https", "moneroblocks.info", "/api/get_stats");
			urlhundert = url100;

			// für Difficulty Ethereum
			URL url101 = new URL("https", "www.etherchain.org", "/api/difficulty");
			urlhunderteins = url101;

			// für Reward Ethereum
			URL url102 = new URL("https", "cryptocompare.com", "/api/data/coinsnapshot/?fsym=ETH&tsym=USD");
			urlhundertzwei = url102;

			// für Währungsberechnungen Euro - US_Dollar
			URL url110 = new URL("https", "free.currencyconverterapi.com", "/api/v5/convert?q=EUR_USD&compact=y");
			urlhundertzehn = url110;

			// für Währungsberechnungen Euro - südkoreanischer Won
			URL url111 = new URL("https", "free.currencyconverterapi.com", "/api/v5/convert?q=EUR_KRW&compact=y");
			urlhundertelf = url111;

		} catch (MalformedURLException mue)
		{
			mue.printStackTrace(System.err);
		}
	}

	// abrufen der Handelsplätze, zerlegen des JSON-Strings mittels Parser und
	// übergabe des passenden Wertes an die passende Liste
	// in der meine Beträge gespeichert sind
	public void getValueAnyCoin() {

		System.setProperty("http.agent", "Chrome");

		for (int a = 0; a < urllistanycoin.size(); a++)
		{
			try (BufferedReader in = new BufferedReader(new InputStreamReader(urllistanycoin.get(a).openStream())))
			{
				String inputLine;

				System.out.println("/***** File content Anycoin *****/n");
				while ((inputLine = in.readLine()) != null)
				{
					// für Testzwecke um Aufbau des JSON-Strings zu untersuchen
					// System.out.println(inputLine);

					JSONParser parser = new JSONParser();
					JSONObject obj2 = (JSONObject) parser.parse(inputLine);

					JSONArray lang = (JSONArray) obj2.get("Data");
					// @SuppressWarnings("rawtypes")
					Iterator i = lang.iterator();

					while (i.hasNext())
					{
						JSONObject innerObj = (JSONObject) i.next();

						betraege.add(a, (double) innerObj.get("FiatAmount"));
						break;
					}
				}
			} catch (IOException ioe)
			{
				ioe.printStackTrace(System.err);
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void getValueBittrex() {

		System.setProperty("http.agent", "Chrome");

		for (int a = 0; a < urllistbittrex.size(); a++)
		{

			try (BufferedReader in = new BufferedReader(new InputStreamReader(urllistbittrex.get(a).openStream())))
			{
				String inputLine;

				System.out.println("/***** File content Bittrex *****/n");
				while ((inputLine = in.readLine()) != null)
				{

					JSONParser parser = new JSONParser();
					JSONObject obj2 = (JSONObject) parser.parse(inputLine);

					JSONArray lang = (JSONArray) obj2.get("result");
					// @SuppressWarnings("rawtypes")
					Iterator i = lang.iterator();

					while (i.hasNext())
					{
						JSONObject innerObj = (JSONObject) i.next();

						betraegebittrex.add(a, (double) innerObj.get("Rate"));
						break;
					}

				}
			}

			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException ioe)
			{
				ioe.printStackTrace(System.err);
			}
		}

	}

	public void getValuePoloniex() {

		System.setProperty("http.agent", "Chrome");

		for (int a = 0; a < urllistpoloniex.size(); a++)
		{

			try (BufferedReader in = new BufferedReader(new InputStreamReader(urllistpoloniex.get(a).openStream())))
			{
				String inputLine;

				System.out.println("/***** File content Poloniex *****/n");
				while ((inputLine = in.readLine()) != null)
				{
					// System.out.println(inputLine);

					JSONParser parser = new JSONParser();
					JSONObject obj2 = (JSONObject) parser.parse(inputLine);

					JSONArray lang = (JSONArray) obj2.get("asks");
					// @SuppressWarnings("rawtypes")
					JSONArray output = (JSONArray) lang.get(0);

					betraegepoloniex.add(a, Double.parseDouble(output.get(0).toString()));

				}
			}

			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException ioe)
			{
				ioe.printStackTrace(System.err);
			}
		}

	}

	public void getValueHitbtc() {

		System.setProperty("http.agent", "Chrome");

		for (int a = 0; a < urllisthitbtc.size(); a++)
		{

			try (BufferedReader in = new BufferedReader(new InputStreamReader(urllisthitbtc.get(a).openStream())))
			{
				String inputLine;

				System.out.println("/***** File content hitbtc *****/n");
				while ((inputLine = in.readLine()) != null)
				{
					// System.out.println(inputLine);

					JSONParser parser = new JSONParser();
					JSONObject obj2 = (JSONObject) parser.parse(inputLine);

					JSONArray lang = (JSONArray) obj2.get("ask");
					// @SuppressWarnings("rawtypes")
					Iterator i = lang.iterator();

					while (i.hasNext())
					{
						JSONObject innerObj = (JSONObject) i.next();

						betraegehitbtc.add(a, Double.parseDouble(innerObj.get("price").toString()));
						break;
					}
				}
			}

			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException ioe)
			{
				ioe.printStackTrace(System.err);
			}
		}

	}

	public void getValueBithumb() {

		System.setProperty("http.agent", "Chrome");

		for (int a = 0; a < urllistbithumb.size(); a++)
		{

			try (BufferedReader in = new BufferedReader(new InputStreamReader(urllistbithumb.get(a).openStream())))
			{
				String inputLine;

				System.out.println("/***** File content bithumb *****/n");
				while ((inputLine = in.readLine()) != null)
				{
					// System.out.println(inputLine);

					JSONParser parser = new JSONParser();
					JSONObject obj2 = (JSONObject) parser.parse(inputLine);

					JSONObject data = (JSONObject) obj2.get("data");
					JSONArray lang = (JSONArray) data.get("asks");
					// @SuppressWarnings("rawtypes")
					Iterator i = lang.iterator();

					while (i.hasNext())
					{
						JSONObject innerObj = (JSONObject) i.next();

						betraegebithumb.add(a, Double.parseDouble(innerObj.get("price").toString()));
						break;
					}
				}
			}

			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException ioe)
			{
				ioe.printStackTrace(System.err);
			}
		}

	}

	public void getValueKraken() {

		System.setProperty("http.agent", "Chrome");

		for (int a = 0; a < urllistkraken.size(); a++)
		{
			try (BufferedReader in = new BufferedReader(new InputStreamReader(urllistkraken.get(a).openStream())))
			{
				String inputLine;

				System.out.println("/***** File content Kraken *****/n");
				while ((inputLine = in.readLine()) != null)
				{

					JSONParser parser = new JSONParser();
					JSONObject obj2 = (JSONObject) parser.parse(inputLine);

					JSONObject result = (JSONObject) obj2.get("result");
					JSONObject crpair = (JSONObject) result.get(krakencodes.get(a));
					JSONArray output = (JSONArray) crpair.get("asks");
					JSONArray ask = (JSONArray) output.get(0);
					// @SuppressWarnings("rawtypes")

					betraegekraken.add(a, Double.parseDouble(ask.get(0).toString()));

				}
			}

			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException ioe)
			{
				ioe.printStackTrace(System.err);
			}
		}

	}

	public void getMoneroDifficulty() {

		System.setProperty("http.agent", "Chrome");

		try (BufferedReader in = new BufferedReader(new InputStreamReader(urlhundert.openStream())))
		{
			String inputLine;

			System.out.println("/***** File content Monero Difficulty + Reward *****/n");
			while ((inputLine = in.readLine()) != null)
			{
				// System.out.println(inputLine);

				JSONParser parser = new JSONParser();
				JSONObject obj2 = (JSONObject) parser.parse(inputLine);

				monerodifficulty = Double.parseDouble(obj2.get("difficulty").toString());
				moneroreward = Double.parseDouble(obj2.get("last_reward").toString());

			}
		}

		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
		}

	}

	public void getEthereumDifficulty() {

		System.setProperty("http.agent", "Chrome");

		try (BufferedReader in = new BufferedReader(new InputStreamReader(urlhunderteins.openStream())))
		{
			String inputLine;

			System.out.println("/***** File content Ethereum Difficulty *****/n");
			while ((inputLine = in.readLine()) != null)
			{
				// System.out.println(inputLine);

				JSONParser parser = new JSONParser();
				JSONArray diffArray = (JSONArray) parser.parse(inputLine);
				JSONObject diffElem = (JSONObject) diffArray.get(0);

				// @SuppressWarnings("rawtypes")

				ethereumdifficulty = Double.parseDouble(diffElem.get("difficulty").toString());

			}
		}

		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
		}

	}

	public void getEthereumReward() {

		System.setProperty("http.agent", "Chrome");

		try (BufferedReader in = new BufferedReader(new InputStreamReader(urlhundertzwei.openStream())))
		{
			String inputLine;

			System.out.println("/***** File content Ethereum Reward *****/n");
			while ((inputLine = in.readLine()) != null)
			{
				// System.out.println(inputLine);

				JSONParser parser = new JSONParser();
				JSONObject obj2 = (JSONObject) parser.parse(inputLine);
				JSONObject diffElem = (JSONObject) obj2.get("Data");

				ethereumreward = Double.parseDouble(diffElem.get("BlockReward").toString());

			}
		}

		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
		}

	}

	public void getDollarKurs() {

		System.setProperty("http.agent", "Chrome");

		try (BufferedReader in = new BufferedReader(new InputStreamReader(urlhundertzehn.openStream())))
		{
			String inputLine;

			System.out.println("/***** Währungsrechner Dollar*****/n");
			while ((inputLine = in.readLine()) != null)
			{
				// System.out.println(inputLine);

				JSONParser parser = new JSONParser();
				JSONObject obj2 = (JSONObject) parser.parse(inputLine);
				JSONObject obj3 = (JSONObject) obj2.get("EUR_USD");

				eurodollar = Double.parseDouble(obj3.get("val").toString());

			}
		}

		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
		}

	}

	public void getWonKurs() {

		System.setProperty("http.agent", "Chrome");

		try (BufferedReader in = new BufferedReader(new InputStreamReader(urlhundertelf.openStream())))
		{
			String inputLine;

			System.out.println("/***** Währungsrechner Won*****/n");
			while ((inputLine = in.readLine()) != null)
			{
				// System.out.println(inputLine);

				JSONParser parser = new JSONParser();
				JSONObject obj2 = (JSONObject) parser.parse(inputLine);
				JSONObject obj3 = (JSONObject) obj2.get("EUR_KRW");

				eurowon = Double.parseDouble(obj3.get("val").toString());

			}
		}

		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
		}

	}

	public static void main(String[] args) throws IOException {

		launch();

	}

	public ImageView getImageViewWithImage(String file) {
		String path = "/multithread/" + file;
		// System.out.println(path);

		InputStream is = this.getClass().getResourceAsStream(path);
		Image image = new Image(is);
		ImageView iv = new ImageView();
		iv.setImage(image);
		iv.setFitWidth(300);
		iv.setPreserveRatio(true);
		iv.setSmooth(true);
		return iv;
	}

	public void start(Stage teststage) {

		// Dies ist ganz wichtig da launch() ein Objekt vom Typ Applikation
		// erzeugt und dieses Objekt muss die Methode getURLs() aufrufen
		this.getURLs();

		// hinzufügen der einzelnen Kürzel welche später abgerufen werden um der
		// JSON-String
		// mittels Parser erfolgreich zu zerlegen - diese Kürzel sind in den
		// jeweiligen String
		// abhängig von der abgerufenen Währung
		this.krakencodes.add("XXBTZEUR");
		this.krakencodes.add("XETHZEUR");
		this.krakencodes.add("XXRPZEUR");
		this.krakencodes.add("BCHEUR");
		this.krakencodes.add("XLTCZEUR");
		this.krakencodes.add("XXMRZEUR");
		this.krakencodes.add("DASHEUR");

		// ausführen der einzelnen tasks über die verfügbaren Threads im
		// ExecutorPool
		future1 = executor.submit(this.task1);
		future2 = executor.submit(this.task2);
		future3 = executor.submit(this.task3);
		future4 = executor.submit(this.task4);
		future5 = executor.submit(this.task5);
		future6 = executor.submit(this.task6);
		future7 = executor.submit(this.task7);
		future8 = executor.submit(this.task8);
		future9 = executor.submit(this.task9);
		future10 = executor.submit(this.task10);
		future11 = executor.submit(this.task11);

		MenuBar menubar = new MenuBar();
		Menu mfile = new Menu("Datei");
		Menu mhelp = new Menu("?");

		menubar.getMenus().addAll(mfile, mhelp);

		MenuItem showmarket = new MenuItem("aktuelle Kurse anzeigen");
		MenuItem calculator = new MenuItem("mining Rechner");
		MenuItem exit = new MenuItem("Exit");
		MenuItem about = new MenuItem("Info");

		mfile.getItems().addAll(showmarket, calculator, exit);
		mhelp.getItems().addAll(about);

		ImageView iv4 = getImageViewWithImage("monero.jpg");
		ImageView iv6 = getImageViewWithImage("ethereum.jpg");
		ImageView iv8 = getImageViewWithImage("trading.jpg");

		exit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				executor.shutdown();
				System.exit(0);
			}
		});

		VBox hb1 = new VBox();

		hb1.getChildren().addAll(menubar);
		hb1.setSpacing(10);

		HBox box1 = new HBox();
		// HBox box2 = new HBox();
		box1.getChildren().add(iv8);
		// box2.getChildren().add(iv2);
		// Pane gibt an wie das Layout gestaltet sein soll
		GridPane pane = new GridPane();

		pane.add(hb1, 0, 0);
		pane.add(box1, 0, 1);
		// pane.setCenter(box1);
		// pane.setBottom(box2);
		stage1 = new Stage();
		scene1 = new Scene(new Group());
		Stage stage2 = new Stage();
		Scene scene2 = new Scene(new Group());
		Stage stage3 = new Stage();
		Scene scene3 = new Scene(new Group());

		showmarket.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {

				try
				{
					// Flaschenhals aber notwendig da handleTable() erst
					// aufgerufen werden darf wenn die Daten
					// vollständig bezogen wurden -> Datenintegrität
					// gewährleistet ist
					String result1 = future1.get();
					String result2 = future2.get();
					String result3 = future3.get();
					String result4 = future4.get();
					String result5 = future5.get();
					String result6 = future6.get();
					String result7 = future7.get();
					String result8 = future8.get();
					String result9 = future9.get();
					String result10 = future10.get();
					String result11 = future11.get();
				} catch (InterruptedException | ExecutionException e)
				{
					e.printStackTrace();
				}

				handleTable();
				executor.shutdown();
			}
		});
		// showmarket.setOnAction((ActionEvent e) -> handleTable());

		calculator.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {

				try
				{
					// Flaschenhals aber notwendig da die kalkulation erst
					// durchgeführt werden darf wenn die Daten
					// vollständig bezogen sind -> Datenintegrität gewährleistet
					// ist
					String result1 = future1.get();
					String result2 = future2.get();
					String result3 = future3.get();
					String result4 = future4.get();
					String result5 = future5.get();
					String result6 = future6.get();
					String result7 = future7.get();
					String result8 = future8.get();
					String result9 = future9.get();
					String result10 = future10.get();
					String result11 = future11.get();
				} catch (InterruptedException | ExecutionException e)
				{
					e.printStackTrace();
				}

				executor.shutdown();

				stage2.show();
				stage2.setTitle("Miningergebnis berechnen");
				stage2.setWidth(380);
				stage2.setHeight(450);

				GridPane pane2 = new GridPane();
				Scene scene2 = new Scene(pane2, 100, 280);
				stage2.setScene(scene2);
				stage2.show();
				pane2.setHgap(10);
				pane2.setVgap(10);
				pane2.setPadding(new Insets(10, 10, 10, 10));

				final Button calculate = new Button("Berechnen");
				Button clear = new Button();
				clear.setText("Clear");

				Text selectcurrencie = new Text("Währung auswählen:");
				pane2.add(selectcurrencie, 0, 0);
				Text hashrate = new Text("Hashrate eingeben:");
				pane2.add(hashrate, 0, 1);
				Text powerusage = new Text("Stromverbrauch in Watt eingeben:");
				pane2.add(powerusage, 0, 2);
				Text powerprice = new Text("Strompreis der kWh angeben:");
				pane2.add(powerprice, 0, 3);
				Text miningprofitd = new Text("Ergebnis pro Tag:");
				Text miningprofitm = new Text("Ergebnis pro Monat:");
				Text miningprofity = new Text("Ergebnis pro Jahr:");
				pane2.add(miningprofitd, 0, 5);
				pane2.add(miningprofitm, 0, 6);
				pane2.add(miningprofity, 0, 7);
				pane2.add(calculate, 0, 4);
				pane2.add(clear, 1, 4);
				TextField tfpowerusage = new TextField();
				pane2.add(tfpowerusage, 1, 2);
				TextField tfpowerprice = new TextField();
				tfpowerprice.setPromptText("In DE ca. 0.285 Cent");
				pane2.add(tfpowerprice, 1, 3);
				TextField tfresultd = new TextField();
				tfresultd.setEditable(false);
				pane2.add(tfresultd, 1, 5);
				TextField tfresultm = new TextField();
				tfresultm.setEditable(false);
				pane2.add(tfresultm, 1, 6);
				TextField tfresulty = new TextField();
				tfresulty.setEditable(false);
				pane2.add(tfresulty, 1, 7);
				TextField tfhashrate = new TextField();
				pane2.add(tfhashrate, 1, 1);
				tfhashrate.getText();
				tfpowerprice.getText();
				tfpowerusage.getText();

				pane2.add(currenciebox, 1, 0);

				// hier sagen wir was die Auswahlliste beinhalten soll
				if (currenciebox.getItems().isEmpty())
				{
					currenciebox.getItems().addAll("Ethereum", "Monero");
				}

				currenciebox.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if (currenciebox.getValue().equals("Ethereum"))
						{
							hashrate.setText("Hashrate eingeben(MH/s):");
							tfhashrate.setPromptText("RX 580 ca. 29 H/s");
							tfpowerusage.setPromptText("RX 580 ca. 130 W");
						}
						if (currenciebox.getValue().equals("Monero"))
						{
							hashrate.setText("Hashrate eingeben(H/s):");
							tfhashrate.setPromptText("Vega 64 ca. 2000 H/s");
							tfpowerusage.setPromptText("Vega 64 ca. 320 W");
						}
					}
				});

				calculate.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						if (currenciebox.getValue().equals("Monero"))
						{
							// Muss durch 1000000000000.0 geteilt werden
							// damit
							// das Komma an der richtigen Stelle ist
							double mrealreward = moneroreward / 1000000000000.0;
							double mpday;
							double input = Double.parseDouble(tfhashrate.getText());
							// hier errechnen wir die generrierten Coins pro
							// Tag
							mpday = ((input * mrealreward) / monerodifficulty) * 3600 * 24;

							double powercostperday = Double.parseDouble(tfpowerusage.getText()) * 24
									* (Double.parseDouble(tfpowerprice.getText()) / 1000);
							// 28,5 Cent/kWh ungeführ der Preis
							// mpday = mphour * 24;

							double gewinn = (mpday * betraegekraken.get(5)) - powercostperday;
							NumberFormat numberFormat = new DecimalFormat("0.00");
							// tfresult.setText(numberFormat.format(monatgewinn).toString());
							// System.out.println(numberFormat.format(gewinn).toString());
							// System.out.println(numberFormat.format(krakenxmr).toString());
							if (gewinn >= 0)
							{

								String sd = (numberFormat.format(gewinn)).toString() + "€ Gewinn";
								String sm = (numberFormat.format(gewinn * 30)).toString() + "€ Gewinn";
								String sy = (numberFormat.format(gewinn * 30 * 12)).toString() + "€ Gewinn";
								tfresultd.setText(sd);
								tfresultm.setText(sm);
								tfresulty.setText(sy);

							}
							if (gewinn < 0)
							{
								String sd = (numberFormat.format(gewinn)).toString() + "€ Verlust";
								String sm = (numberFormat.format(gewinn * 30)).toString() + "€ Verlust";
								String sy = (numberFormat.format(gewinn * 30 * 12)).toString() + "€ Verlust";
								tfresultd.setText(sd);
								tfresultm.setText(sm);
								tfresulty.setText(sy);
							}

						}
						if (currenciebox.getValue().equals("Ethereum"))
						{
							// System.out.println("in der Ethereum
							// Auswahl");

							double erealreward = ethereumreward;
							double epday;
							double input = Double.parseDouble(tfhashrate.getText()) * 1000000;
							// hier errechnen wir die generrierten Coins pro
							// Tag
							epday = ((input * erealreward) / ethereumdifficulty) * 3600 * 24;

							double powercostperday = Double.parseDouble(tfpowerusage.getText()) * 24
									* (Double.parseDouble(tfpowerprice.getText()) / 1000);
							// 28,5 Cent/kWh ungeführ der Preis
							// mpday = mphour * 24;

							double gewinn = (epday * betraegekraken.get(1)) - powercostperday;
							NumberFormat numberFormat = new DecimalFormat("0.00");
							// tfresult.setText(numberFormat.format(monatgewinn).toString());
							// System.out.println(numberFormat.format(gewinn).toString());
							// System.out.println(numberFormat.format(krakeneth).toString());
							if (gewinn >= 0)
							{

								String sd = (numberFormat.format(gewinn)).toString() + "€ Gewinn";
								String sm = (numberFormat.format(gewinn * 30)).toString() + "€ Gewinn";
								String sy = (numberFormat.format(gewinn * 30 * 12)).toString() + "€ Gewinn";
								tfresultd.setText(sd);
								tfresultm.setText(sm);
								tfresulty.setText(sy);

							}
							if (gewinn < 0)
							{
								String sd = (numberFormat.format(gewinn)).toString() + "€ Verlust";
								String sm = (numberFormat.format(gewinn * 30)).toString() + "€ Verlust";
								String sy = (numberFormat.format(gewinn * 30 * 12)).toString() + "€ Verlust";
								tfresultd.setText(sd);
								tfresultm.setText(sm);
								tfresulty.setText(sy);
							}

						}

					}

				});

				clear.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						tfhashrate.clear();
						tfpowerprice.clear();
						tfpowerusage.clear();
						tfresultd.clear();
						tfresultm.clear();
						tfresulty.clear();
					}
				});

				// Daten von hier übernommen
				// https://whattomine.com/coins/151-eth-ethash

				// ripple kann nicht gemint werden
				// Formel für die Anzahl der generierten Moneros pro Stunde
				// Profit = ((hashrate * block_reward) / current_difficulty)
				// *
				// (1 - pool_fee) * 3600

			}
		});

		about.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {

				stage3.show();
				stage3.setTitle("Credits");
				stage3.setWidth(320);
				stage3.setHeight(280);

				GridPane pane4 = new GridPane();
				Scene scene4 = new Scene(pane4, stage3.getWidth(), stage3.getHeight());

				Label label2 = new Label("Lizenz: GPLv3 Entwickler: Stefan H.");
				label2.setFont(new Font("Arial", 16));
				HBox box3 = new HBox();
				HBox box4 = new HBox();
				box3.getChildren().add(iv4);
				box4.getChildren().add(iv6);
				pane4.add(label2, 0, 0);
				pane4.add(box3, 0, 1);
				pane4.add(box4, 0, 3);
				TextField moneroadress = new TextField();
				moneroadress.setText(
						"48siVbnz2YWai8sBs8ZaHZ1hKDvMRwAtURV5TYFS85M9LuyjjHr6HdNg8Fk5zoS3LXDH5EPZoU9DDEDA45JYjV2SVNNghdX");
				moneroadress.setEditable(false);
				pane4.add(moneroadress, 0, 2);
				TextField etheradress = new TextField();
				etheradress.setText("0x00191620f69309543b4DC1DD27dF2b833e168976");
				etheradress.setEditable(false);
				pane4.add(etheradress, 0, 4);

				stage3.setScene(scene4);
				// stage3.setResizable(false);
				stage3.show();

			}
		});

		// in dem Fall wohl eine Art brnach node weil ein StackPane beispiel
		// darunter ist
		Scene sceneone = new Scene(pane, 300, 250);

		teststage.setScene(sceneone);

		teststage.setTitle("Virtual Currency Monitor");
		// wird benötigt am das Fenster sichtbar zu machen selbe wie set
		// visible(true) bei swing
		teststage.show();

	}

	Stage stage1;
	Scene scene1;

	public void handleTable() {

		stage1.setTitle("aktuelle Kurse");
		stage1.setWidth(1200);
		stage1.setHeight(500);

		GridPane pane5 = new GridPane();
		Scene scene1 = new Scene(pane5, stage1.getWidth(), stage1.getHeight());

		final Label label = new Label("günstigste Angebote in Euro umgerechnet:");
		label.setFont(new Font("Arial", 20));

		table.setEditable(false);
		table2.setEditable(false);

		NumberFormat numberFormat = new DecimalFormat("0.00");
		Marktplatz mplatz1 = new Marktplatz("Anycoin Direct (Europa)", numberFormat.format(betraege.get(0)).toString(),
				numberFormat.format(betraege.get(1)).toString(), "nicht gelistet", "nicht gelistet",
				numberFormat.format(betraege.get(2)).toString(), numberFormat.format(betraege.get(3)).toString(),
				numberFormat.format(betraege.get(4)).toString());
		Marktplatz mplatz2 = new Marktplatz("Bittrex (USA)",
				numberFormat.format(betraegebittrex.get(0) / eurodollar).toString(),
				numberFormat.format(betraegebittrex.get(1) / eurodollar).toString(),
				numberFormat.format(betraegebittrex.get(2) / eurodollar).toString(),
				numberFormat.format(betraegebittrex.get(3) / eurodollar).toString(),
				numberFormat.format(betraegebittrex.get(4) / eurodollar).toString(),
				numberFormat.format(betraegebittrex.get(5) / eurodollar).toString(),
				numberFormat.format(betraegebittrex.get(6) / eurodollar).toString());
		Marktplatz mplatz3 = new Marktplatz("Poloniex (USA)",
				numberFormat.format(betraegepoloniex.get(0) / eurodollar).toString(),
				numberFormat.format(betraegepoloniex.get(1) / eurodollar).toString(),
				numberFormat.format(betraegepoloniex.get(2) / eurodollar).toString(),
				numberFormat.format(betraegepoloniex.get(3) / eurodollar).toString(),
				numberFormat.format(betraegepoloniex.get(4) / eurodollar).toString(),
				numberFormat.format(betraegepoloniex.get(5) / eurodollar).toString(),
				numberFormat.format(betraegepoloniex.get(6) / eurodollar).toString());
		Marktplatz mplatz4 = new Marktplatz("HitBTC (Asien)",
				numberFormat.format(betraegehitbtc.get(0) / eurodollar).toString(),
				numberFormat.format(betraegehitbtc.get(1) / eurodollar).toString(),
				numberFormat.format(betraegehitbtc.get(2) / eurodollar).toString(),
				numberFormat.format(betraegehitbtc.get(3) / eurodollar).toString(),
				numberFormat.format(betraegehitbtc.get(4) / eurodollar).toString(),
				numberFormat.format(betraegehitbtc.get(5) / eurodollar).toString(),
				numberFormat.format(betraegehitbtc.get(6) / eurodollar).toString());
		Marktplatz mplatz5 = new Marktplatz("bithumb (Südkorea)",
				numberFormat.format(betraegebithumb.get(0) / eurowon).toString(),
				numberFormat.format(betraegebithumb.get(1) / eurowon).toString(),
				numberFormat.format(betraegebithumb.get(2) / eurowon).toString(),
				numberFormat.format(betraegebithumb.get(3) / eurowon).toString(),
				numberFormat.format(betraegebithumb.get(4) / eurowon).toString(),
				numberFormat.format(betraegebithumb.get(5) / eurowon).toString(),
				numberFormat.format(betraegebithumb.get(6) / eurowon).toString());
		Marktplatz mplatz6 = new Marktplatz("Kraken (Europa)", numberFormat.format(betraegekraken.get(0)).toString(),
				numberFormat.format(betraegekraken.get(1)).toString(),
				numberFormat.format(betraegekraken.get(2)).toString(),
				numberFormat.format(betraegekraken.get(3)).toString(),
				numberFormat.format(betraegekraken.get(4)).toString(),
				numberFormat.format(betraegekraken.get(5)).toString(),
				numberFormat.format(betraegekraken.get(6)).toString());

		double maxbtc = Math.max(Math.max(
				Math.max(
						Math.max(
								Math.max(betraege.get(0).doubleValue(),
										(betraegebittrex.get(0).doubleValue() / eurodollar)),
								(betraegepoloniex.get(0).doubleValue() / eurodollar)),
						(betraegehitbtc.get(0).doubleValue() / eurodollar)),
				(betraegebithumb.get(0).doubleValue() / eurowon)), betraegekraken.get(0).doubleValue());
		double maxeth = Math.max(Math.max(
				Math.max(
						Math.max(
								Math.max(betraege.get(1).doubleValue(),
										(betraegebittrex.get(1).doubleValue() / eurodollar)),
								(betraegepoloniex.get(1).doubleValue() / eurodollar)),
						(betraegehitbtc.get(1).doubleValue() / eurodollar)),
				(betraegebithumb.get(1).doubleValue() / eurowon)), betraegekraken.get(1).doubleValue());
		double maxxrp = Math.max(
				Math.max(
						Math.max(
								Math.max(betraegekraken.get(2).doubleValue(),
										(betraegebittrex.get(2).doubleValue() / eurodollar)),
								(betraegepoloniex.get(2).doubleValue() / eurodollar)),
						(betraegehitbtc.get(2).doubleValue() / eurodollar)),
				(betraegebithumb.get(2).doubleValue() / eurowon));
		double maxbcc = Math.max(
				Math.max(
						Math.max(
								Math.max(betraegekraken.get(3).doubleValue(),
										(betraegebittrex.get(3).doubleValue() / eurodollar)),
								(betraegepoloniex.get(3).doubleValue() / eurodollar)),
						(betraegehitbtc.get(3).doubleValue() / eurodollar)),
				(betraegebithumb.get(3).doubleValue() / eurowon));
		double maxltc = Math.max(Math.max(
				Math.max(
						Math.max(
								Math.max(betraege.get(2).doubleValue(),
										(betraegebittrex.get(4).doubleValue() / eurodollar)),
								(betraegepoloniex.get(4).doubleValue() / eurodollar)),
						(betraegehitbtc.get(4).doubleValue() / eurodollar)),
				(betraegebithumb.get(4).doubleValue() / eurowon)), betraegekraken.get(4).doubleValue());
		double maxxmr = Math.max(Math.max(
				Math.max(
						Math.max(
								Math.max(betraege.get(3).doubleValue(),
										(betraegebittrex.get(5).doubleValue() / eurodollar)),
								(betraegepoloniex.get(5).doubleValue() / eurodollar)),
						(betraegehitbtc.get(5).doubleValue() / eurodollar)),
				(betraegebithumb.get(5).doubleValue() / eurowon)), betraegekraken.get(5).doubleValue());
		double maxdash = Math.max(Math.max(
				Math.max(
						Math.max(
								Math.max(betraege.get(4).doubleValue(),
										(betraegebittrex.get(6).doubleValue() / eurodollar)),
								(betraegepoloniex.get(6).doubleValue() / eurodollar)),
						(betraegehitbtc.get(6).doubleValue() / eurodollar)),
				(betraegebithumb.get(6).doubleValue() / eurowon)), betraegekraken.get(6).doubleValue());

		double minbtc = Math.min(Math.min(
				Math.min(
						Math.min(
								Math.min(betraege.get(0).doubleValue(),
										(betraegebittrex.get(0).doubleValue() / eurodollar)),
								(betraegepoloniex.get(0).doubleValue() / eurodollar)),
						(betraegehitbtc.get(0).doubleValue() / eurodollar)),
				(betraegebithumb.get(0).doubleValue() / eurowon)), betraegekraken.get(0).doubleValue());
		double mineth = Math.min(Math.min(
				Math.min(Math.min(Math.min(betraege.get(1), (betraegebittrex.get(1) / eurodollar)),
						(betraegepoloniex.get(1) / eurodollar)), (betraegehitbtc.get(1) / eurodollar)),
				(betraegebithumb.get(1) / eurowon)), betraegekraken.get(1));
		double minxrp = Math.min(
				Math.min(Math.min(Math.min(betraegekraken.get(2), (betraegebittrex.get(2) / eurodollar)),
						(betraegepoloniex.get(2) / eurodollar)), (betraegehitbtc.get(2) / eurodollar)),
				(betraegebithumb.get(2) / eurowon));
		double minbcc = Math.min(
				Math.min(Math.min(Math.min(betraegekraken.get(3), (betraegebittrex.get(3) / eurodollar)),
						(betraegepoloniex.get(3) / eurodollar)), (betraegehitbtc.get(3) / eurodollar)),
				(betraegebithumb.get(3) / eurowon));
		double minltc = Math.min(Math.min(
				Math.min(Math.min(Math.min(betraege.get(2), (betraegebittrex.get(4) / eurodollar)),
						(betraegepoloniex.get(4) / eurodollar)), (betraegehitbtc.get(4) / eurodollar)),
				(betraegebithumb.get(4) / eurowon)), betraegekraken.get(4));
		double minxmr = Math.min(Math.min(
				Math.min(Math.min(Math.min(betraege.get(3), (betraegebittrex.get(5) / eurodollar)),
						(betraegepoloniex.get(5) / eurodollar)), (betraegehitbtc.get(5) / eurodollar)),
				(betraegebithumb.get(5) / eurowon)), betraegekraken.get(5));
		double mindash = Math.min(Math.min(
				Math.min(Math.min(Math.min(betraege.get(4), (betraegebittrex.get(6) / eurodollar)),
						(betraegepoloniex.get(6) / eurodollar)), (betraegehitbtc.get(6) / eurodollar)),
				(betraegebithumb.get(6) / eurowon)), betraegekraken.get(6));

		Marktplatz mplatz7 = new Marktplatz("MAX:", numberFormat.format(maxbtc).toString(),
				numberFormat.format(maxeth).toString(), numberFormat.format(maxxrp).toString(),
				numberFormat.format(maxbcc).toString(), numberFormat.format(maxltc).toString(),
				numberFormat.format(maxxmr).toString(), numberFormat.format(maxdash).toString());

		Marktplatz mplatz8 = new Marktplatz("MIN:", numberFormat.format(minbtc).toString(),
				numberFormat.format(mineth).toString(), numberFormat.format(minxrp).toString(),
				numberFormat.format(minbcc).toString(), numberFormat.format(minltc).toString(),
				numberFormat.format(minxmr).toString(), numberFormat.format(mindash).toString());

		Marktplatz mplatz9 = new Marktplatz("SPREAD:",
				numberFormat.format((maxbtc * 100 / minbtc) - 100.0).toString() + "%",
				numberFormat.format((maxeth * 100 / mineth) - 100.0).toString() + "%",
				numberFormat.format((maxxrp * 100 / minxrp) - 100.0).toString() + "%",
				numberFormat.format((maxbcc * 100 / minbcc) - 100.0).toString() + "%",
				numberFormat.format((maxltc * 100 / minltc) - 100.0).toString() + "%",
				numberFormat.format((maxxmr * 100 / minxmr) - 100.0).toString() + "%",
				numberFormat.format((maxdash * 100 / mindash) - 100.0).toString() + "%");

		// System.out.println(mplatz1);

		final ObservableList<Marktplatz> data = FXCollections.observableArrayList(mplatz1, mplatz2, mplatz3, mplatz4,
				mplatz5, mplatz6);

		final ObservableList<Marktplatz> data2 = FXCollections.observableArrayList(mplatz8, mplatz7, mplatz9);

		String maximumchart = "Maximum";
		String minimumchart = "Minimum";

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);

		bc.setTitle("Minimum, Maximum -> Spread");
		xAxis.setLabel("Kennzahlen");
		yAxis.setLabel("Wert");

		XYChart.Series<String, Number> series1 = new XYChart.Series<>();
		series1.setName("Minimum");
		series1.getData().add(new XYChart.Data<String, Number>(minimumchart,
				((minbtc + mineth + minxrp + minbcc + minltc + minxmr + mindash) / 7)));

		XYChart.Series<String, Number> series2 = new XYChart.Series<>();
		series2.setName("Maximum");
		series2.getData().add(new XYChart.Data<String, Number>(maximumchart,
				((maxbtc + maxeth + maxxrp + maxbcc + maxltc + maxxmr + maxdash) / 7)));

		bc.getData().addAll(series1, series2);

		TableColumn<Marktplatz, String> HandelNameCol = new TableColumn<>("Marktplatzname");
		TableColumn<Marktplatz, String> BitcoinCol = new TableColumn<>("Bitcoin");
		TableColumn<Marktplatz, String> EtherCol = new TableColumn<>("Etherium");
		TableColumn<Marktplatz, String> RippleCol = new TableColumn<>("Ripple");
		TableColumn<Marktplatz, String> BtCashCol = new TableColumn<>("Bitcoin Cash");
		TableColumn<Marktplatz, String> LitecoinCol = new TableColumn<>("Litecoin");
		TableColumn<Marktplatz, String> MoneroCol = new TableColumn<>("Monero");
		TableColumn<Marktplatz, String> DashCol = new TableColumn<>("Dash");

		TableColumn<Marktplatz, String> StatistikNameCol = new TableColumn<>("Statistiken");
		TableColumn<Marktplatz, String> Bitcoin2Col = new TableColumn<>("Bitcoin");
		TableColumn<Marktplatz, String> Ether2Col = new TableColumn<>("Etherium");
		TableColumn<Marktplatz, String> Ripple2Col = new TableColumn<>("Ripple");
		TableColumn<Marktplatz, String> BtCash2Col = new TableColumn<>("Bitcoin Cash");
		TableColumn<Marktplatz, String> Litecoin2Col = new TableColumn<>("Litecoin");
		TableColumn<Marktplatz, String> Monero2Col = new TableColumn<>("Monero");
		TableColumn<Marktplatz, String> Dash2Col = new TableColumn<>("Dash");

		// bezieht die Daten aus den getter Methoden in Klasse Marktplatz
		HandelNameCol.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("mplatzname"));
		BitcoinCol.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("bcbetrag"));
		EtherCol.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("ethbetrag"));
		RippleCol.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("xrpbetrag"));
		BtCashCol.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("bchbetrag"));
		LitecoinCol.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("ltcbetrag"));
		MoneroCol.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("xmrbetrag"));
		DashCol.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("dashbetrag"));

		StatistikNameCol.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("mplatzname"));
		Bitcoin2Col.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("bcbetrag"));
		Ether2Col.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("ethbetrag"));
		Ripple2Col.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("xrpbetrag"));
		BtCash2Col.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("bchbetrag"));
		Litecoin2Col.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("ltcbetrag"));
		Monero2Col.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("xmrbetrag"));
		Dash2Col.setCellValueFactory(new PropertyValueFactory<Marktplatz, String>("dashbetrag"));

		table.setItems(data);
		table2.setItems(data2);

		if (table.getColumns().isEmpty())
		{
			table.getColumns().addAll(HandelNameCol, BitcoinCol, EtherCol, RippleCol, BtCashCol, LitecoinCol, MoneroCol,
					DashCol);
		}
		if (table2.getColumns().isEmpty())
		{
			table2.getColumns().addAll(StatistikNameCol, Bitcoin2Col, Ether2Col, Ripple2Col, BtCash2Col, Litecoin2Col,
					Monero2Col, Dash2Col);
		}

		final VBox vbox = new VBox();
		final VBox vbox2 = new VBox();
		final VBox vbox3 = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(label, table);
		vbox2.setSpacing(5);
		vbox2.setPadding(new Insets(10, 0, 0, 10));
		vbox2.getChildren().addAll(table2);
		vbox3.setSpacing(5);
		vbox3.setPadding(new Insets(10, 0, 0, 10));
		vbox3.getChildren().addAll(bc);

		// ((Group) scene1.getRoot()).getChildren().addAll(vbox, vbox2);
		pane5.add(vbox, 0, 0);
		pane5.add(vbox2, 0, 1);
		pane5.add(vbox3, 1, 1);

		stage1.setScene(scene1);
		stage1.show();
	}

	public static class Marktplatz {

		String mplatzname = null;
		String bcbetrag = null;
		String ethbetrag = null;
		String xrpbetrag = null;
		String bchbetrag = null;
		String ltcbetrag = null;
		String xmrbetrag = null;
		String dashbetrag = null;

		private Marktplatz(String mplatzname, String bcbetrag, String ethbetrag, String xrpbetrag, String bchbetrag,
				String ltcbetrag, String xmrbetrag, String dashbetrag) {
			this.mplatzname = mplatzname;
			this.bcbetrag = bcbetrag;
			this.ethbetrag = ethbetrag;
			this.xrpbetrag = xrpbetrag;
			this.bchbetrag = bchbetrag;
			this.ltcbetrag = ltcbetrag;
			this.xmrbetrag = xmrbetrag;
			this.dashbetrag = dashbetrag;

		}

		public String getMplatzname() {
			return mplatzname;
		}

		public String getBcbetrag() {
			return bcbetrag;
		}

		public String getEthbetrag() {
			return ethbetrag;
		}

		public String getXrpbetrag() {
			return xrpbetrag;
		}

		public String getBchbetrag() {
			return bchbetrag;
		}

		public String getLtcbetrag() {
			return ltcbetrag;
		}

		public String getXmrbetrag() {
			return xmrbetrag;
		}

		public String getDashbetrag() {
			return dashbetrag;
		}

		public void setMplatzName(String mplatzname) {
			this.mplatzname = mplatzname;
		}

		public void setBcbetrag(String bcbetrag) {
			this.bcbetrag = bcbetrag;
		}

		public void setEthbetrag(String ethbetrag) {
			this.ethbetrag = ethbetrag;
		}

		public void setXrpbetrag(String xrpbetrag) {
			this.xrpbetrag = xrpbetrag;
		}

		public void setBchbetrag(String bchbetrag) {
			this.bchbetrag = bchbetrag;
		}

		public void setLtcbetrag(String ltcbetrag) {
			this.ltcbetrag = ltcbetrag;
		}

		public void setXmrbetrag(String xmrbetrag) {
			this.xmrbetrag = xmrbetrag;
		}

		public void setDashbetrag(String dashbetrag) {
			this.dashbetrag = dashbetrag;
		}

		@Override
		public String toString() {
			return "Marktplatz [mplatzname=" + mplatzname + ", bcbetrag=" + bcbetrag + ", ethbetrag=" + ethbetrag
					+ ", xrpbetrag=" + xrpbetrag + ", bchbetrag=" + bchbetrag + ", ltcbetrag=" + ltcbetrag
					+ ", xmrbetrag=" + xmrbetrag + ", dashbetrag=" + dashbetrag + "]";
		}

	}
}
