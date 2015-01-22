package de.jmf.awagner.taschenrechner;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TaschenRechner extends ActionBarActivity implements View.OnClickListener {

    // 0: zurückgesetzt, 1: Zahl wird eingegeben, 2: Rechenzeichen wurde eingegeben.
    private int zustand = 0;
    // speichert die Zwischenergebnisse des letzten Rechenschritts.
    private double zwischenergebnis = 0;
    // speichert das letzte eingegebene Rechenzeichen (Werte 0 , 10, 11, 12, 13, 14)
    private int rechenzeichen = 0;
    // Array zum Übersetzen von button ids in Zahlen ids.
    private SparseArray<Integer> button2id;
    // Das Display des Recheners
    private TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taschen_rechner);

        // hole die Referenzen auf Viewobjekte
        display = (TextView) this.findViewById(R.id.display);
        // falls der Inhalt zu lang wird als Scrollfenster anbiete.
        display.setMovementMethod(new ScrollingMovementMethod());

        // die Zuordnung von Resourceid zu einer ganzen Zahl hinterlegen
        // damit wird die Ereignisbehandlung (onClick) stark vereinfacht
        //
        button2id = new SparseArray<Integer>();
        button2id.put(R.id.b0, Integer.valueOf(0));
        button2id.put(R.id.b1, Integer.valueOf(1));
        button2id.put(R.id.b2, Integer.valueOf(2));
        button2id.put(R.id.b3, Integer.valueOf(3));
        button2id.put(R.id.b4, Integer.valueOf(4));
        button2id.put(R.id.b5, Integer.valueOf(5));
        button2id.put(R.id.b6, Integer.valueOf(6));
        button2id.put(R.id.b7, Integer.valueOf(7));
        button2id.put(R.id.b8, Integer.valueOf(8));
        button2id.put(R.id.b9, Integer.valueOf(9));
        button2id.put(R.id.add, Integer.valueOf(10));
        button2id.put(R.id.sub, Integer.valueOf(11));
        button2id.put(R.id.mult, Integer.valueOf(12));
        button2id.put(R.id.div, Integer.valueOf(13));
        button2id.put(R.id.equal, Integer.valueOf(14));
        button2id.put(R.id.clear, Integer.valueOf(15));

        // Die Activity für alle Buttons als OnClickListener registrieren.
        for (int i = 0; i < 16; i++) {
            Button b = (Button) this.findViewById(button2id.keyAt(i));
            b.setTextSize(50);
            // Übergeben diese Activity als OnClickListener
            // (Button ruft künftig bei Klick die implementierte Methode onClick dieser Klasse auf)
            b.setOnClickListener(this);
        }
    }

    @Override
    /** Ereignisbehandlung aller Buttonklicks, implementiert ein (etwas komplizierteres) Zustands-
     *  diagramm.
     *
     *  @param View v die View, in der die Buttons enthalten sind.
     */
    public void onClick(View v) {

        Integer integer = button2id.get(v.getId());
        if (integer == null) {
            display.setText(Integer.toString(v.getId()));
            return;
        }

        int id = integer.intValue();

        switch (zustand) {

            case 0:
                // Zahlentaste.
                if (id < 10) {
                    anfuegenZiffer(id);
                    zustand = 1;
                }
                break;

            case 1:
                if (id < 10) {
                    anfuegenZiffer(id);
                    zustand = 1;
                }
                // Rechentaste.
                if ((id < 15) && (id > 9)) {
                    berechneZwischenwert();
                    speichereRechenzeichen(id);
                    zustand = 2;
                }
                // Cleartaste.
                if (id == 15) {
                    zurueckSetzen();
                    zustand = 0;
                }
                break;

            case 2:
                if (id < 10) {
                    loescheDisplay();
                    anfuegenZiffer(id);
                    zustand = 1;
                }
                // Rechentaste.
                if ((id < 14) && (id > 9)) {
                    speichereRechenzeichen(id);
                }
                if (id == 14) {
                    berechneZwischenwert();

                }
                // Cleartaste.
                if (id == 15) {
                    zurueckSetzen();
                    zustand = 0;
                }
                break;
        }
    }

    /** berechne Zwischenergebnis, wenn Rechenzeichen eingegeben werden. */
    public void berechneZwischenwert() {
        switch (rechenzeichen) {
            case 14:
            case 0:
                zwischenergebnis = leseDisplay();
                break;
            case 10:
                zwischenergebnis += leseDisplay();
                break;
            case 11:
                zwischenergebnis -= leseDisplay();
                break;
            case 12:
                zwischenergebnis = zwischenergebnis * leseDisplay();
                break;
            case 13:
                zwischenergebnis = zwischenergebnis / leseDisplay();
                break;
        }
        display.setText(String.valueOf(zwischenergebnis));
    }

    /** gib den Wert, der im Display steht als Zahl zurück*/
    public double leseDisplay() {
        String displayAnzeige = display.getText().toString();
        return Double.parseDouble(displayAnzeige);
    }

    /** löscht das Display */
    public void loescheDisplay() {
        display.setText("");
    }

    /** fügt an die aktuelle Displayanzeige eine Ziffer an */
    public void anfuegenZiffer(int ziffer){
        String anzeigetext = display.getText().toString();
        display.setText(anzeigetext + ziffer);
    }

    /** das Rechenzeichen für bis zur Eingabe der nächsten Rechenoperation speichern
     *
     * @param id die ID des Rechenzeichen (0, 10, 11, 12, 13, 14)
     */
    public void speichereRechenzeichen(int id) {
        rechenzeichen = id;
    }

    /** setzt den Rechner zuück (zustand, zwischenergebnis, rechenzeichen, Display */
    public void zurueckSetzen() {
        zustand = 0;
        zwischenergebnis = 0;
        rechenzeichen = 0;
        loescheDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_taschen_rechner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
