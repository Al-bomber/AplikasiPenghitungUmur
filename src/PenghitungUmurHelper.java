
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Period;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.JTextArea;
import java.io.UnsupportedEncodingException;
import java.util.function.Supplier;




/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ACER
 */
public class PenghitungUmurHelper {

    public String hitungUmurDetail(LocalDate lahir, LocalDate sekarang) {
        Period period;
        period = Period.between(lahir, sekarang);
        return period.getYears() + " tahun, " + period.getMonths() + " bulan, " + period.getDays() + " hari";
    }

    public LocalDate hariUlangTahunBerikutnya(LocalDate lahir, LocalDate sekarang) {
        LocalDate ulangTahunBerikutnya = lahir.withYear(sekarang.getYear());
        if (!ulangTahunBerikutnya.isAfter(sekarang)) {
            ulangTahunBerikutnya = ulangTahunBerikutnya.plusYears(1);
        }
        return ulangTahunBerikutnya;
    }

    public String getDayOfWeekInIndonesian(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY:
                return "Senin";
            case TUESDAY:
                return "Selasa";
            case WEDNESDAY:
                return "Rabu";
            case THURSDAY:
                return "Kamis";
            case FRIDAY:
                return "Jum'at";
            case SATURDAY:
                return "Sabtu";
            case SUNDAY:
                return "Minggu";
            default:
                return "";
        }
    }
    
    public void getPeristiwaBarisPerBaris(LocalDate tanggal, JTextArea txtAreaPeristiwa, Supplier<Boolean> shouldStop) {
        try {
            String urlString = "https://byabbe.se/on-this-day/" + tanggal.getMonthValue() + "/" + tanggal.getDayOfMonth() + "/events.json";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            JSONArray events = json.getJSONArray("events");
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                String year = event.getString("year");
                String description = event.getString("description");
                String translatedDescription = translateToIndonesian(description);
                String peristiwa = year + ": " + translatedDescription;
                
                javax.swing.SwingUtilities.invokeLater(() ->
                txtAreaPeristiwa.append(peristiwa + "\n"));
                txtAreaPeristiwa.append(year + ": " + description + "\n");
            }
        } catch (Exception e) {
            txtAreaPeristiwa.setText("Gagal mendapatkan data peristiwa.");
        }
    }
    
    public String translateToIndonesian(String text) {
        try {
            String urlString = "https://lingva.ml/api/v1/en/id/" + URLEncoder.encode(text, "UTF-8");
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            return json.getString("translation");
        } catch (Exception e) {
            return text + " (Gagal diterjemahkan)";
        }
    }
    
}