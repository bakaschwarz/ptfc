package de.yabue.bakacore.Configurations;

import javafx.beans.property.*;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.util.HashMap;

/**
 * Diese Klasse bietet eine Möglichkeit, Werte aus einer {@code .properties} auszulesen und diese in
 * {@link javafx.beans.value.ObservableValue} zu kapseln.
 * Man kann sowohl die Property, als auch den Wert selbst anfordern.
 * Allerdings müssen Nutzer dieser Klasse wissen, um was für eine Art Wert es sich handelt, wenn sie
 * die Property anfordern.
 */
public class ObservableConfiguration {

    @Getter
    private final File CONFIG;

    private final PropertiesConfiguration PROPERTIES_CONFIGURATION;

    private final HashMap<String, Property> MAP;

    @Getter
    private SimpleBooleanProperty printInfosProperty;

    @Getter
    private SimpleBooleanProperty autoSaveProperty;

    /**
     * Konstruiert eine neue Konfigurierung anhand einer übergebenen Properties Datei.
     * @param pathToConfiguration Pfad zu einer Properties Datei.
     * @param autoSave Wenn {@code true}, werden Änderungen an den Properties gespeichert.
     */
    public ObservableConfiguration(@NonNull File pathToConfiguration, boolean autoSave) throws ConfigurationException {
        CONFIG = pathToConfiguration;
        PROPERTIES_CONFIGURATION = new PropertiesConfiguration(pathToConfiguration);
        PROPERTIES_CONFIGURATION.setAutoSave(autoSave);
        MAP = new HashMap<>();
        System.out.println("Configuration initialized...");
        autoSaveProperty = new SimpleBooleanProperty(autoSave);
        printInfosProperty = new SimpleBooleanProperty(false);
        if(autoSave){
            System.out.println("Changes on the configuration will be automatically saved.");
        }else{
            System.out.println("Changes on the configuration will not be saved.");
        }
    }

    /**
     * Konstruiert eine leere Konfiguration.
     */
    public ObservableConfiguration(){
        CONFIG = null;
        PROPERTIES_CONFIGURATION = new PropertiesConfiguration();
        MAP = new HashMap<>();
        printInfosProperty = new SimpleBooleanProperty(false);
        System.out.println("Eine leere Konfiguration wurde initialisiert...");
    }

    /**
     * Liefert zu einem übergebenen Schlüssel einen observierbaren Wert zurück. Wird keiner gefunden, so wird mit dem
     * übergebenen Standardwert ein observierbarer Wert erstellt. Diese Methode fügt allerdings keine Werte und Schlüssel
     * permanent in die Properties Datei ein.
     * @param keyWord Der Schlüssel, zu dem ein observierbarer Wert gesucht werden soll.
     * @param defaultValue Ein Wert der genutzt wird, wenn in der Konfigurierungsdatei kein passender Wert oder Schlüssel vorhanden ist.
     * @return Eine Referenz auf einen observierbaren Wert aus der Properties Datei, oder auf den Standartwert.
     */
    public SimpleStringProperty getStringProperty(@NonNull final String keyWord, @NonNull String defaultValue){
        SimpleStringProperty result;
        if(MAP.get(keyWord) == null){
            if(PROPERTIES_CONFIGURATION.getProperty(keyWord) != null){
                result = new SimpleStringProperty((String) PROPERTIES_CONFIGURATION.getProperty(keyWord));
                if (printInfosProperty.getValue())
                    System.out.println("Schlüssel ist nicht in der Map, füge "+keyWord+" hinzu...");
            }else{
                result = new SimpleStringProperty(defaultValue);
                if (printInfosProperty.getValue()) {
                    System.out.println("Schlüssel ist nicht in der Map und der Schlüssel existiert nicht oder hat keinen Wert...");
                    System.out.println("Füge temporär einen Standartwert für " + keyWord + " ein...");
                }
            }
            MAP.put(keyWord, result);
        }else{
            result = (SimpleStringProperty) MAP.get(keyWord);
            if (printInfosProperty.getValue())
                System.out.println("Wert für "+keyWord+" gefunden...");
        }
        return result;
    }

    /**
     * Liefert zu einem übergebenen Schlüssel einen observierbaren Wert zurück. Wird keiner gefunden, so wird mit dem
     * übergebenen Standardwert ein observierbarer Wert erstellt. Diese Methode fügt allerdings keine Werte und Schlüssel
     * permanent in die Properties Datei ein.
     * @param keyWord Der Schlüssel, zu dem ein observierbarer Wert gesucht werden soll.
     * @param defaultValue Ein Wert der genutzt wird, wenn in der Konfigurierungsdatei kein passender Wert oder Schlüssel vorhanden ist.
     * @return Eine Referenz auf einen observierbaren Wert aus der Properties Datei, oder auf den Standartwert.
     */
    public SimpleIntegerProperty getIntegerProperty(@NonNull final String keyWord, @NonNull Integer defaultValue){
        SimpleIntegerProperty result;
        if(MAP.get(keyWord) == null){
            if(PROPERTIES_CONFIGURATION.getProperty(keyWord) != null){
                result = new SimpleIntegerProperty(Integer.parseInt((String) PROPERTIES_CONFIGURATION.getProperty(keyWord)));
                if (printInfosProperty.getValue())
                    System.out.println("Schlüssel ist nicht in der Map, füge "+keyWord+" hinzu...");
            }else{
                result = new SimpleIntegerProperty(defaultValue);
                if (printInfosProperty.getValue()) {
                    System.out.println("Schlüssel ist nicht in der Map und der Schlüssel existiert nicht oder hat keinen Wert...");
                    System.out.println("Füge temporär einen Standartwert für " + keyWord + " ein...");
                }
            }
            MAP.put(keyWord, result);
        }else{
            result = (SimpleIntegerProperty) MAP.get(keyWord);
            if (printInfosProperty.getValue())
                System.out.println("Wert für "+keyWord+" gefunden...");
        }
        return result;
    }

    /**
     * Liefert zu einem übergebenen Schlüssel einen observierbaren Wert zurück. Wird keiner gefunden, so wird mit dem
     * übergebenen Standardwert ein observierbarer Wert erstellt. Diese Methode fügt allerdings keine Werte und Schlüssel
     * permanent in die Properties Datei ein.
     * @param keyWord Der Schlüssel, zu dem ein observierbarer Wert gesucht werden soll.
     * @param defaultValue Ein Wert der genutzt wird, wenn in der Konfigurierungsdatei kein passender Wert oder Schlüssel vorhanden ist.
     * @return Eine Referenz auf einen observierbaren Wert aus der Properties Datei, oder auf den Standartwert.
     */
    public SimpleDoubleProperty getDoubleProperty(@NonNull final String keyWord, @NonNull Double defaultValue){
        SimpleDoubleProperty result;
        if(MAP.get(keyWord) == null){
            if(PROPERTIES_CONFIGURATION.getProperty(keyWord) != null){
                result = new SimpleDoubleProperty(Double.parseDouble((String) PROPERTIES_CONFIGURATION.getProperty(keyWord)));
                if (printInfosProperty.getValue())
                    System.out.println("Schlüssel ist nicht in der Map, füge "+keyWord+" hinzu...");
            }else{
                result = new SimpleDoubleProperty(defaultValue);
                if (printInfosProperty.getValue()) {
                    System.out.println("Schlüssel ist nicht in der Map und der Schlüssel existiert nicht oder hat keinen Wert...");
                    System.out.println("Füge temporär einen Standartwert für " + keyWord + " ein...");
                }
            }
            MAP.put(keyWord, result);
        }else{
            result = (SimpleDoubleProperty) MAP.get(keyWord);
            if (printInfosProperty.getValue())
                System.out.println("Wert für "+keyWord+" gefunden...");
        }
        return result;
    }

    /**
     * Liefert zu einem übergebenen Schlüssel einen observierbaren Wert zurück. Wird keiner gefunden, so wird mit dem
     * übergebenen Standardwert ein observierbarer Wert erstellt. Diese Methode fügt allerdings keine Werte und Schlüssel
     * permanent in die Properties Datei ein.
     * @param keyWord Der Schlüssel, zu dem ein observierbarer Wert gesucht werden soll.
     * @param defaultValue Ein Wert der genutzt wird, wenn in der Konfigurierungsdatei kein passender Wert oder Schlüssel vorhanden ist.
     * @return Eine Referenz auf einen observierbaren Wert aus der Properties Datei, oder auf den Standartwert.
     */
    public SimpleBooleanProperty getBooleanProperty(@NonNull final String keyWord, @NonNull boolean defaultValue){
        SimpleBooleanProperty result;
        if(MAP.get(keyWord) == null){
            if(PROPERTIES_CONFIGURATION.getProperty(keyWord) != null){
                result = new SimpleBooleanProperty(Boolean.parseBoolean((String) PROPERTIES_CONFIGURATION.getProperty(keyWord)));
                if (printInfosProperty.getValue())
                    System.out.println("Schlüssel ist nicht in der Map, füge "+keyWord+" hinzu...");
            }else{
                result = new SimpleBooleanProperty(defaultValue);
                if (printInfosProperty.getValue()) {
                    System.out.println("Schlüssel ist nicht in der Map und der Schlüssel existiert nicht oder hat keinen Wert...");
                    System.out.println("Füge temporär einen Standartwert für " + keyWord + " ein...");
                }
            }
            MAP.put(keyWord, result);
        }else{
            result = (SimpleBooleanProperty) MAP.get(keyWord);
            if (printInfosProperty.getValue())
                System.out.println("Wert für "+keyWord+" gefunden...");
        }
        return result;
    }

    /**
     * Liefert zu einem übergebenen Schlüssel den passenden Wert zurück. Wird keiner gefunden, wird der übergebene
     * Ersatzwert zurückgegeben.
     * @param keyWord Der Schlüssel, zu dem ein Wert geladen werden soll.
     * @param defaultValue Der Ausweichwert, falls in der Properties Datei nichts gefunden wurde.
     * @return
     */
    public String getString(@NonNull final String keyWord, @NonNull String defaultValue) {
        String result;
        if(PROPERTIES_CONFIGURATION.getString(keyWord) != null) {
            result = PROPERTIES_CONFIGURATION.getString(keyWord);
            if (printInfosProperty.getValue())
                System.out.println("Wert für " + keyWord + " gefunden...");
        }else {
            result = defaultValue;
            if (printInfosProperty.getValue())
                System.out.println("Kein Wert für "+keyWord+" gefunden...");
        }
        return result;
    }

    /**
     * Liefert zu einem übergebenen Schlüssel den passenden Wert zurück. Wird keiner gefunden, wird der übergebene
     * Ersatzwert zurückgegeben.
     * @param keyWord Der Schlüssel, zu dem ein Wert geladen werden soll.
     * @param defaultValue Der Ausweichwert, falls in der Properties Datei nichts gefunden wurde.
     * @return
     */
    public Integer getInteger(@NonNull final String keyWord, @NonNull Integer defaultValue) {
        Integer result;
        if(PROPERTIES_CONFIGURATION.getString(keyWord) != null) {
            result = PROPERTIES_CONFIGURATION.getInt(keyWord);
            if (printInfosProperty.getValue())
                System.out.println("Wert für " + keyWord + " gefunden...");
        }else {
            result = defaultValue;
            if (printInfosProperty.getValue())
                System.out.println("Kein Wert für "+keyWord+" gefunden...");
        }
        return result;
    }

    /**
     * Liefert zu einem übergebenen Schlüssel den passenden Wert zurück. Wird keiner gefunden, wird der übergebene
     * Ersatzwert zurückgegeben.
     * @param keyWord Der Schlüssel, zu dem ein Wert geladen werden soll.
     * @param defaultValue Der Ausweichwert, falls in der Properties Datei nichts gefunden wurde.
     * @return
     */
    public Double getDouble(@NonNull final String keyWord, @NonNull Double defaultValue) {
        Double result;
        if(PROPERTIES_CONFIGURATION.getString(keyWord) != null) {
            result = PROPERTIES_CONFIGURATION.getDouble(keyWord);
            if (printInfosProperty.getValue())
                System.out.println("Wert für " + keyWord + " gefunden...");
        }else {
            result = defaultValue;
            if (printInfosProperty.getValue())
                System.out.println("Kein Wert für "+keyWord+" gefunden...");
        }
        return result;
    }

    /**
     * Liefert zu einem übergebenen Schlüssel den passenden Wert zurück. Wird keiner gefunden, wird der übergebene
     * Ersatzwert zurückgegeben.
     * @param keyWord Der Schlüssel, zu dem ein Wert geladen werden soll.
     * @param defaultValue Der Ausweichwert, falls in der Properties Datei nichts gefunden wurde.
     * @return
     */
    public Boolean getBoolean(@NonNull final String keyWord, @NonNull Boolean defaultValue) {
        Boolean result;
        if(PROPERTIES_CONFIGURATION.getString(keyWord) != null) {
            result = PROPERTIES_CONFIGURATION.getBoolean(keyWord);
            if (printInfosProperty.getValue())
                System.out.println("Wert für " + keyWord + " gefunden...");
        }else {
            result = defaultValue;
            if (printInfosProperty.getValue())
                System.out.println("Kein Wert für "+keyWord+" gefunden...");
        }
        return result;
    }

    /**
     * Setzt einen Wert für ein übergebenes Schlüsselwort.
     * @param keyWord Der Schlüssel, zu dem ein observierbarer Wert zugewiesen werden soll.
     * @param value Der Wert, der dem Schlüssel zugewiesen werden soll.
     */
    public SimpleStringProperty setStringProperty(@NonNull final String keyWord, @NonNull final String value){
        SimpleStringProperty property;
        if( PROPERTIES_CONFIGURATION.getProperty(keyWord) == null){
            property = new SimpleStringProperty(value);
            MAP.put(keyWord, property);
        }else{
            property = getStringProperty(keyWord, value);
        }
        PROPERTIES_CONFIGURATION.setProperty(keyWord, value);
        if (printInfosProperty.getValue())
            System.out.println("Setze neuen Wert für "+keyWord+"...");
        return property;
    }

    /**
     * Setzt einen Wert für ein übergebenes Schlüsselwort.
     * @param keyWord Der Schlüssel, zu dem ein observierbarer Wert zugewiesen werden soll.
     * @param value Der Wert, der dem Schlüssel zugewiesen werden soll.
     */
    public SimpleIntegerProperty setIntegerProperty(@NonNull final String keyWord, @NonNull final Integer value){
        SimpleIntegerProperty property;
        if( PROPERTIES_CONFIGURATION.getProperty(keyWord) == null){
            property = new SimpleIntegerProperty(value);
            MAP.put(keyWord, property);
        }else{
            property = getIntegerProperty(keyWord, value);
        }
        PROPERTIES_CONFIGURATION.setProperty(keyWord, value);
        if (printInfosProperty.getValue())
            System.out.println("Setze neuen Wert für "+keyWord+"...");
        return property;
    }

    /**
     * Setzt einen Wert für ein übergebenes Schlüsselwort.
     * @param keyWord Der Schlüssel, zu dem ein observierbarer Wert zugewiesen werden soll.
     * @param value Der Wert, der dem Schlüssel zugewiesen werden soll.
     */
    public SimpleDoubleProperty setDoubleProperty(@NonNull final String keyWord, @NonNull final Double value){
        SimpleDoubleProperty property;
        if( PROPERTIES_CONFIGURATION.getProperty(keyWord) == null){
            property = new SimpleDoubleProperty(value);
            MAP.put(keyWord, property);
        }else{
            property = getDoubleProperty(keyWord, value);
        }
        PROPERTIES_CONFIGURATION.setProperty(keyWord, value);
        if (printInfosProperty.getValue())
            System.out.println("Setze neuen Wert für "+keyWord+"...");
        return property;
    }

    /**
     * Setzt einen Wert für ein übergebenes Schlüsselwort.
     * @param keyWord Der Schlüssel, zu dem ein observierbarer Wert zugewiesen werden soll.
     * @param value Der Wert, der dem Schlüssel zugewiesen werden soll.
     */
    public SimpleBooleanProperty setBooleanProperty(@NonNull final String keyWord, @NonNull final boolean value){
        SimpleBooleanProperty property;
        if( PROPERTIES_CONFIGURATION.getProperty(keyWord) == null){
            property = new SimpleBooleanProperty(value);
            MAP.put(keyWord, property);
        }else{
            property = getBooleanProperty(keyWord, value);
        }
        PROPERTIES_CONFIGURATION.setProperty(keyWord, value);
        if (printInfosProperty.getValue())
            System.out.println("Setze neuen Wert für "+keyWord+"...");
        return property;
    }

    /**
     * Setzen dieses Wertes entscheidet, ob Änderungen an der Properties Datei permanent sind, oder nicht.
     * @param autoSave {@code true}, wenn Änderungen gespeichert werden sollen.
     */
    public void setAutoSaveProperty(final boolean autoSave){
        autoSaveProperty.setValue(autoSave);
        if (printInfosProperty.getValue())
            System.out.println("Speicherfunktion der Konfiguration wird auf "+Boolean.toString(autoSave)+" gesetzt...");
    }

    /**
     * Setzt das Encoding Format für die Konfiguration. Standard ist <code>System.getProperty("file.encoding")</code>
     * @param encoding Das gewünschte Encoding Format
     */
    public void setEncoding(final String encoding) {
        PROPERTIES_CONFIGURATION.setEncoding(encoding);
    }
}
