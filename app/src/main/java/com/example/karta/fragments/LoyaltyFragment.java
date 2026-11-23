package com.example.karta.fragments;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import com.example.karta.database.DBHelper;
import com.example.karta.models.User;
import com.example.karta.utils.*;

/** Полностью локальная демонстрация программы лояльности. */
public class LoyaltyFragment extends Fragment {
    private DBHelper db;
    private User user;
    private LinearLayout body;
    private int page;
    private final int gold = Color.rgb(235,190,114), white = Color.rgb(255,247,237);
    public static LoyaltyFragment page(int page) {
        LoyaltyFragment f = new LoyaltyFragment(); Bundle args = new Bundle(); args.putInt("page",page); f.setArguments(args); return f;
    }
    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density); }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        db = new DBHelper(requireContext()); page = requireArguments().getInt("page");
        ScrollView scroll = new ScrollView(requireContext()); scroll.setFillViewport(true); scroll.setBackgroundColor(Color.rgb(26,16,19));
        body = new LinearLayout(requireContext()); body.setOrientation(LinearLayout.VERTICAL); body.setPadding(dp(22),dp(24),dp(22),dp(28)); scroll.addView(body);
        render(); return scroll;
    }
    private TextView text(LinearLayout parent, String value, int size, int color) {
        TextView v = new TextView(requireContext()); v.setText(value); v.setTextSize(size); v.setTextColor(color); v.setPadding(0,dp(5),0,dp(8)); parent.addView(v); return v;
    }
    private LinearLayout card() {
        LinearLayout box = new LinearLayout(requireContext()); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(20),dp(16),dp(20),dp(16));
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(79,29,40),Color.rgb(48,24,31)}); bg.setCornerRadius(dp(24)); bg.setStroke(dp(1),Color.rgb(110,65,64)); box.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2); lp.bottomMargin=dp(16); body.addView(box,lp); return box;
    }
    private void button(LinearLayout parent, String label, Runnable action) {
        Button b = new Button(requireContext()); b.setText(label); b.setAllCaps(false); b.setTextColor(Color.rgb(35,20,24)); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gold)); parent.addView(b,new LinearLayout.LayoutParams(-1,dp(56))); b.setOnClickListener(v->action.run());
    }
    private void render() {
        body.removeAllViews(); user = db.getUserById(new SessionManager(requireContext()).getUserId()); if(user==null) return;
        text(body,"М / МАТРЁШКА",15,gold).setTypeface(null,Typeface.BOLD);
        text(body,page==0 ? "Выгода внутри." : page==1 ? "Демо-касса" : "История покупок",32,white).setTypeface(null,Typeface.BOLD);
        text(body,page==0 ? "Здравствуйте, " + user.getName() : "Учебный прототип · без реальных платежей",14,Color.rgb(190,169,173));
        if(page==0) home(); else if(page==1) shop(); else history();
    }
    private void home() {
        long total=db.totalPaid(user.getId());
        LinearLayout c=card(); text(c,"ВАША БОНУСНАЯ КАРТА",12,gold); text(c,user.getBonusBalance()+" Б",46,white).setTypeface(null,Typeface.BOLD);
        text(c,"1 бонус = 1 рубль скидки",14,white); text(c,user.getFormattedCardNumber(),20,gold);
        text(c,user.isCardActive()?"● Карта активна":"○ Активируется с первой покупкой",13,white);
        button(c,"Показать QR-код",this::qr);
        LinearLayout level=card(); text(level,BonusRules.level(total)+" уровень · "+BonusRules.rate(total)+"%",22,gold);
        long next=total<5000?5000:15000;
        text(level,total>=15000?"Максимальный уровень достигнут":"До следующего уровня: "+(next-total)+" ₽ покупок",14,white);
        ProgressBar progress=new ProgressBar(requireContext(),null,android.R.attr.progressBarStyleHorizontal); progress.setMax((int)next); progress.setProgress((int)Math.min(total,next)); level.addView(progress);
        text(level,"Бронза 3% → Серебро 5% от 5 000 ₽ → Золото 7% от 15 000 ₽",13,white);
        button(body,"Попробовать покупку",()->checkout("Покупка на демо-кассе",1000));
        LinearLayout rules=card(); text(rules,"Как это работает",21,gold); text(rules,"1. Покажите карту на кассе.\n2. Получайте 3–7% от оплаченной суммы.\n3. Оплачивайте бонусами до 30% покупки.\n\nНовый уровень действует со следующей покупки. Бонусы округляются вниз. Данные сохраняются только на этом устройстве.",15,white);
    }
    private void qr() {
        LinearLayout box=new LinearLayout(requireContext()); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(24),dp(16),dp(24),dp(16)); box.setGravity(Gravity.CENTER);
        ImageView image=new ImageView(requireContext()); image.setImageBitmap(QRCodeGenerator.generateQRCode(user.getCardNumber(),600)); image.setContentDescription("QR-код бонусной карты"); box.addView(image,new LinearLayout.LayoutParams(dp(240),dp(240)));
        TextView number=new TextView(requireContext()); number.setText(user.getFormattedCardNumber()); number.setTextSize(20); number.setGravity(Gravity.CENTER); box.addView(number);
        new AlertDialog.Builder(requireContext()).setTitle("Карта «Матрёшка»").setView(box).setPositiveButton("Готово",null).show();
    }
    private void shop() {
        text(body,"Выберите пример покупки или введите свою сумму. Оплата здесь имитируется.",15,white);
        product("Чайная пауза","Чай, печенье и немного уюта",500);
        product("Дом, милый дом","Товары для домашнего комфорта",2000);
        product("Большая корзина","Удобно показать рост уровня",5000);
        button(body,"Своя сумма покупки",()->checkout("Покупка на демо-кассе",1000));
    }
    private void product(String title,String subtitle,int price) { LinearLayout c=card(); text(c,title,23,gold); text(c,subtitle,14,white); button(c,"Купить за "+price+" ₽",()->checkout(title,price)); }
    private void checkout(String title,int price) {
        user=db.getUserById(user.getId());
        LinearLayout form=new LinearLayout(requireContext()); form.setOrientation(LinearLayout.VERTICAL); form.setPadding(dp(24),dp(8),dp(24),0);
        TextView hint=new TextView(requireContext()); hint.setText("Баланс: "+user.getBonusBalance()+" Б. Можно списать до 30% суммы.\n1 Б = 1 ₽. Начисление: "+BonusRules.rate(db.totalPaid(user.getId()))+"% от оплаты."); form.addView(hint);
        EditText amount=new EditText(requireContext()); amount.setInputType(InputType.TYPE_CLASS_NUMBER); amount.setHint("Сумма в рублях"); amount.setText(String.valueOf(price)); form.addView(amount);
        EditText redeem=new EditText(requireContext()); redeem.setInputType(InputType.TYPE_CLASS_NUMBER); redeem.setHint("Списать бонусы"); redeem.setText("0"); form.addView(redeem);
        AlertDialog dialog=new AlertDialog.Builder(requireContext()).setTitle(title).setView(form).setNegativeButton("Отмена",null).setPositiveButton("Провести покупку",null).create();
        dialog.setOnShowListener(x->dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            Integer a=Validator.parseWhole(amount.getText().toString()), r=Validator.parseWhole(redeem.getText().toString());
            if(a==null || Validator.amountError(a)!=null) { amount.setError("Введите целую сумму от 1 до 100 000"); return; }
            if(r==null) { redeem.setError("Введите целое число бонусов, 0 — без списания"); return; }
            try {
                String receipt=db.purchase(user.getId(),title,a,r); dialog.dismiss(); render();
                new AlertDialog.Builder(requireContext()).setTitle("Покупка проведена").setMessage(receipt+"\n\nДемонстрация: деньги не списывались.").setPositiveButton("Готово",null).show();
            } catch(IllegalArgumentException e) { redeem.setError(e.getMessage()); }
        })); dialog.show();
    }
    private void history() {
        java.util.List<String> entries=db.history(user.getId());
        if(entries.isEmpty()) { LinearLayout c=card(); text(c,"Здесь появится ваша выгода",23,gold); text(c,"Проведите первую покупку на демо-кассе. В чеке будут сумма оплаты, начисление и списание бонусов.",16,white); }
        for(String entry:entries) text(card(),entry,16,white);
    }
    @Override public void onDestroyView() { super.onDestroyView(); db.close(); body=null; }
}
