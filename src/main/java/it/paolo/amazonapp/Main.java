package it.paolo.amazonapp;

import it.paolo.amazonapp.entities.Product;
import it.paolo.amazonapp.service.retriever.Retriever;
import java.util.List;
import org.apache.log4j.Logger;

public class Main {

  public enum Folders {
    ABBA,
    CAVALLI;
  }

  public static final String FOLDER_BASE_PATH = "D:\\AmazonProductImageRetriever";
  public static final int NUMERO_DI_PRODOTTI_PER_FOLDER = 10;
  public static final String API_KEY_BOT_TELEGRAM = "953739920:AAHQSgxmtcHGR3nFuT2wCLyMa0zZhQMnVx0";
  public static final String TELEGRAM_CHANNEL_NAME = "@AmazonReviewJasper";
  public static final String BOT_TELEGRAM_NAME = TELEGRAM_CHANNEL_NAME + "bot";
  public static final String TELEGRAM_ACCOUNT = "@JasperAmazon";

  public static final String GoogleApiClientId = "126345561344-dqmfh9a82qtubn80l7v6rkg0rf14h28l.apps.googleusercontent.com";
  public static final String GoogleApiClientSecret = "wCRNM7tRk9PhSxZnAcPXq0Lw";


  private static final Logger LOGGER = Logger.getLogger(Main.class);

  public static void main(String[] args) {
    LOGGER.info("Start MAIN");

    Retriever app = new Retriever(FOLDER_BASE_PATH);
    //    List<Product> productNameAndLink = app.readProductNameAndLinkFromCsvCavalli();
    List<Product> productNameAndLink = app.readProductNameAndLinkFromCsvAbba();
    app.modularSave(productNameAndLink, Folders.ABBA, 10);
    //    List<Product> productNameAndLink = app.readProductNameAndLinkFromCsvCavalli();
    //    List<Product> coversAndScreen = app.separateCoverAndScreen(productNameAndLink);

    //    productNameAndLink.forEach(app::sendProductOnTelegramChannel);

    LOGGER.info("End MAIN");
  }

}
