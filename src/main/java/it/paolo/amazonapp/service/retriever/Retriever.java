package it.paolo.amazonapp.service.retriever;

import static it.paolo.amazonapp.Main.API_KEY_BOT_TELEGRAM;
import static it.paolo.amazonapp.Main.TELEGRAM_ACCOUNT;
import static it.paolo.amazonapp.Main.TELEGRAM_CHANNEL_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.paolo.amazonapp.Main;
import it.paolo.amazonapp.Main.Folders;
import it.paolo.amazonapp.entities.Product;
import it.paolo.amazonapp.entities.images.ColorImages;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Retriever {

  private static final Logger LOGGER = Logger.getLogger(Retriever.class);
  private static final String EMPTY_SEPARATOR = "VUOTO";
  private final String folderBasePath;

  public Retriever(String folderBasePath) {
    this.folderBasePath = folderBasePath;
  }

  public List<Product> readProductNameAndLinkFromCsvAbba() {
    LOGGER.info("Enter method readProductNameAndLinkFromCsv");
    List<Product> list = new ArrayList<>();

    ClassLoader classLoader = Retriever.class.getClassLoader();
    File file = new File(classLoader.getResource("productsAbba.csv").getFile());

    final String PUNTO_E_VIRGOLA = ";";

    if (file.exists() && file.canRead()) {
      try {
        List<String> strings = Files.readAllLines(file.toPath());
        strings.forEach(line -> {
          line = line.replace(PUNTO_E_VIRGOLA + PUNTO_E_VIRGOLA, PUNTO_E_VIRGOLA + EMPTY_SEPARATOR + PUNTO_E_VIRGOLA);
          String[] split = line.split(PUNTO_E_VIRGOLA);
          Product product = new Product();
          split[0] = split[0].replace("/", " e ");

          product.setName(split[0]);
          LOGGER.info("Product Name: " + product.getName());
          product.setCode(split[1]);
          product.setSellerShop(split[2]);
          product.setPezzi(split[5]);
          product.setUrl(split[7]);
          product.setPpFee(split[8]);

          if (!product.getPezzi().equalsIgnoreCase("stop") && !product.getPezzi().equalsIgnoreCase("0")) {
            list.add(product);
          }
        });

      } catch (IOException e) {
        LOGGER.warn("Error reading file", e);
      }
    }
    LOGGER.info("Exit method readProductNameAndLinkFromCsv");
    return list;

  }

  public List<Product> readProductNameAndLinkFromCsvCavalli() {
    LOGGER.info("Enter method readProductNameAndLinkFromCsv");
    List<Product> list = new ArrayList<>();

    ClassLoader classLoader = Retriever.class.getClassLoader();
    File file = new File(classLoader.getResource("productsCavalli.csv").getFile());

    final String PUNTO_E_VIRGOLA = ";";

    if (file.exists() && file.canRead()) {
      try {
        List<String> strings = Files.readAllLines(file.toPath());
        strings.forEach(line -> {
          line = line.replace(PUNTO_E_VIRGOLA + PUNTO_E_VIRGOLA, PUNTO_E_VIRGOLA + EMPTY_SEPARATOR + PUNTO_E_VIRGOLA);
          String[] split = line.split(PUNTO_E_VIRGOLA);
          Product product = new Product();
          split[0] = split[0].replace("/", " e ");

          if (!"".equals(split[0]) && !"".equals(split[2]) && !"".equals(split[1]) && !EMPTY_SEPARATOR.equals(split[1])) {
            product.setName(split[0]);
            product.setUrl(split[1]);
            product.setSellerShop(split[2]);
            product.setPezzi(split[3]);
            product.setPpFee(split[7]);
            product.setNote(split[9]);

            if (!product.getPezzi().equalsIgnoreCase("stop") && !product.getPezzi().equalsIgnoreCase("0")) {
              list.add(product);
            }
          }

        });

      } catch (IOException e) {
        LOGGER.warn("Error reading file", e);
      }
    }
    LOGGER.info("Exit method readProductNameAndLinkFromCsv");
    return list;

  }

  //Testing method
  public void saveTagToFile(Element element, String fileName) {
    File f = new File(fileName);
    try (PrintStream p = new PrintStream(f)) {
      if (f.createNewFile()) {
        p.println(element);
      }
    } catch (FileNotFoundException e) {
      LOGGER.warn("File not found", e);
    } catch (IOException e) {
      LOGGER.warn("Cannot write on File", e);
    }
  }

  public List<String> readProductUrlFromFile(String fileName) {
    LOGGER.info("Enter method readProductUrlFromFile");
    List<String> links = new ArrayList<>();

    ClassLoader classLoader = Main.class.getClassLoader();

    File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());

    if (file.exists() && file.canRead()) {
      try {
        links = Files.readAllLines(file.toPath());
      } catch (IOException e) {
        LOGGER.warn("Error reading file", e);
      }
    }
    LOGGER.info("Exit method readProductUrlFromFile");
    return links;
  }

  public List<ColorImages> getImagesFromUrlProduct(String url) {

    LOGGER.info("Enter method parseUrl");
    List<ColorImages> colorImages = new ArrayList<>();

    try {
      Document doc = Jsoup.connect(url).maxBodySize(0).timeout(10000).get();

      final String SCRIPT = "script";
      final String COLOR_IMAGES = "colorImages";

      while (!doc.getElementsByTag(SCRIPT).toString().contains(COLOR_IMAGES)) {
        doc = Jsoup.connect(url).maxBodySize(0).timeout(10000).get();
      }

      Element element =
        doc.getElementsByTag(SCRIPT).stream().filter(s -> s.toString().contains(COLOR_IMAGES)).collect(Collectors.toList()).get(0);

      colorImages = extractColorImages(element);

    } catch (IOException e) {
      LOGGER.warn("Error parsing url", e);
    }
    LOGGER.info("Exit method parseUrl");
    return colorImages;
  }

  private List<ColorImages> extractColorImages(Element element) {

    String elementToString = element.toString().substring(element.toString().indexOf('[') + 1).replaceAll("\n", "");

    elementToString = elementToString.substring(0, elementToString.indexOf("}]},"));

    ObjectMapper objectMapper = new ObjectMapper();

    List<ColorImages> colorImages = new ArrayList<>();

    while (elementToString.contains("},{")) {

      String singleElement = elementToString.substring(0, elementToString.indexOf("},{") + 1);

      try {
        colorImages.add(objectMapper.readValue(singleElement, ColorImages.class));
      } catch (IOException e) {
        LOGGER.warn("Error convert Json to ColorImages", e);
      }
      elementToString = elementToString.substring(elementToString.indexOf("},{") + 2);
    }
    return colorImages;
  }

  public void saveImagesToFile(String folderName, List<ColorImages> imgUrlList, String productName) {
    LOGGER.info("Enter method saveImagesToFile");
    for (int i = 0; i < imgUrlList.size(); i++) {
      ColorImages imgUrl = imgUrlList.get(i);
      try {
        String img = imgUrl.getHiRes();
        if (img == null) {
          img = imgUrl.getLarge();
        }

        URL url = new URL(img);
        BufferedImage image = ImageIO.read(url);
        String pathName = String.format("%s\\%s\\%s", folderBasePath, folderName, productName + i + ".jpg");
        ImageIO.write(image, "jpg", new File(pathName));
      } catch (IOException e) {
        LOGGER.warn("Image Exception", e);
      }
    }
    LOGGER.info("Exit method saveImagesToFile");
  }

  public boolean createFolder(String folderName) {
    LOGGER.info("Enter method createFolder");

    String newFolderPath = String.format("%s\\%s", folderBasePath, folderName);
    File f = new File(newFolderPath);

    if (f.exists() && f.isDirectory()) {
      try {
        Files.delete(f.toPath());
        LOGGER.info("Folder deleted");
      } catch (IOException e) {
        LOGGER.warn("Cannot deleted folder");
      }
    }

    LOGGER.info("Exit method createFolder");
    return f.mkdir();
  }

  public void modularSave(List<Product> productNameAndLink, Folders baseFolderNameEnum, int folderSize) {

    int contatore = 0;
    String baseFolderName = baseFolderNameEnum.toString() + "_" + System.currentTimeMillis();
    this.createFolder(baseFolderName);

    for (Iterator<Product> iterator = productNameAndLink.iterator(); iterator.hasNext(); ) {
      String folderName = baseFolderName + "\\" + contatore;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("[IT ONLY]\n\nNuovi Prodotti in vendita, contattami con il tuo profilo Amazon\n\n");

      //creo la cartella
      this.createFolder(folderName);

      //salvo le immagini, una per prodotto, nella cartella
      for (int i = 0; i < folderSize && iterator.hasNext(); i++) {
        Product product = iterator.next();
        List<ColorImages> imagesFromUrlProduct = this.getImagesFromUrlProduct(product.getUrl());
        if (!imagesFromUrlProduct.isEmpty()) {
          this.saveImagesToFile(folderName, Collections.singletonList(imagesFromUrlProduct.get(0)), product.getName());
        }
        stringBuilder.append("* ").append(product.getName());
        if ("si".equalsIgnoreCase(product.getPpFee())) {
          stringBuilder.append(" - copre PP fee.");
        } else {
          stringBuilder.append(" - ").append(product.getPpFee()).append(" PP fee.");
        }
        stringBuilder.append("\n");
        LOGGER.info("Process completed for Product: " + product.getName());
      }

      //creo un file Note nella cartella
      String fileName = String.format("%s\\%s\\%s", folderBasePath, folderName, "0000" + contatore + ".txt");
      Path file = Paths.get(fileName);
      try {
        Files.write(file, Collections.singleton(stringBuilder.toString()), StandardCharsets.UTF_8);
      } catch (IOException e) {
        LOGGER.warn("Error creating note File", e);
      }

      contatore++;
    }
  }

  public void sendProductOnTelegramChannel(Product product) {
    final String NEW_LINE = "%0A";

    List<ColorImages> imagesFromUrlProduct = this.getImagesFromUrlProduct(product.getUrl());

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Prodotto da Testare in cambio di recensione a 5 STELLE")
      .append(NEW_LINE)
      .append(NEW_LINE)
      .append("Articolo:  ")
      .append(product.getName())
      .append(NEW_LINE)
      .append("Copre pp FEE:  ")
      .append(product.getPpFee())
      .append(NEW_LINE)
      .append("Codice Prodotto: ")
      .append(product.getCode())
      .append(NEW_LINE)
      .append("Link al Prodotto:  ")
      .append(product.getUrl())
      .append(NEW_LINE)
      .append(NEW_LINE)
      .append("Per acquistare contattare: ")
      .append(TELEGRAM_ACCOUNT)
      .append(", con il codice del prodotto.")
      .append(NEW_LINE);

    if (!imagesFromUrlProduct.isEmpty()) {
      String imgUrl = imagesFromUrlProduct.get(0).getLarge();
      sendPhoto(imgUrl, stringBuilder.toString());
    } else {
      sendMessage(stringBuilder.toString());
    }
  }

  private void sendMessage(String text) {
    String sendMessage = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    text = text.replace(" ", "%20");
    String urlString = String.format(sendMessage, API_KEY_BOT_TELEGRAM, TELEGRAM_CHANNEL_NAME, text);

    telegramApiCall(urlString);
  }

  private void sendPhoto(String imgUrl, String captionText) {
    String sendPhoto = "https://api.telegram.org/bot%s/sendPhoto?chat_id=%s&photo=%s&caption=%s";

    captionText = captionText.replace(" ", "%20");

    String urlString = String.format(sendPhoto, API_KEY_BOT_TELEGRAM, TELEGRAM_CHANNEL_NAME, imgUrl, captionText);
    telegramApiCall(urlString);

  }

  private void telegramApiCall(String urlString) {
    try {
      URL url = new URL(urlString);
      URLConnection conn = url.openConnection();

      StringBuilder sb = new StringBuilder();
      InputStream is = new BufferedInputStream(conn.getInputStream());
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String inputLine;
      while ((inputLine = br.readLine()) != null) {
        sb.append(inputLine);
      }
      String response = sb.toString();
      LOGGER.info(response);
    } catch (IOException e) {
      LOGGER.warn("IOException", e);
    }
  }

  public List<Product> separateCoverAndScreen(List<Product> productNameAndLink) {
    List<Product> coversAndScreens = new ArrayList<>();

    productNameAndLink.forEach(product -> {
      if (product.getName().contains("cover") || product.getName().contains("custodia") || product.getName().contains("vetro")) {
        coversAndScreens.add(product);
      }
    });

    coversAndScreens.forEach(productNameAndLink::remove);

    return coversAndScreens;
  }
}



