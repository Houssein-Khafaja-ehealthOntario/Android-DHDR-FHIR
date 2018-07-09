package ca.ehealth.ontario.dhdr_fhirprototype.customdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ca.ehealth.ontario.dhdr_fhirprototype.R;

/**
 * This class is used to show dialogs with error messages, possibly used when an exception is thrown.
 */
public class ExceptionErrorDialog extends Dialog implements View.OnClickListener
{
    private String errorMessage;

    public ExceptionErrorDialog(Activity activity)
    {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // no title required
        setContentView(R.layout.custom_dialog_exception_error_dialog);

        // find our button and error text view
        Button okayButton = findViewById(R.id.errorOkayButton);
        TextView errorItemTextView = findViewById(R.id.errorItem);

        //attaching listeners to dialog views
        okayButton.setOnClickListener(this);

        // set the error message to the text view
        errorItemTextView.setText(errorMessage);
    }

    /**
     * simple onClick listener implementation.
     * Will close the dialog when the "okay" button is pressed.
     * @param view that was pressed
     */
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.errorOkayButton:
                dismiss();
                break;

            default:
                dismiss();
                break;
        }
    }

    /**
     * simple function to show the dialog with specified error message.
     * @param errorMessage
     */
    public void showErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
        this.show();
    }
}
