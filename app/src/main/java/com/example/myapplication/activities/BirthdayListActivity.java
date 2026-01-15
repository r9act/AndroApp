package com.example.myapplication.activities;

import static com.example.myapplication.activities.MainActivity.EXTRA_BIRTHDAYS_LIST;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.BirthdayAdapter;
import com.example.myapplication.entity.PersonBirthday;
import com.example.myapplication.R;
import com.example.myapplication.utils.BirthdayDiffCallback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BirthdayListActivity extends AppCompatActivity {
    public static final String EMPTY = "----------";
    List<PersonBirthday> personBirthdays;
    BirthdayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthdays);

        personBirthdays = (List<PersonBirthday>) getIntent().getSerializableExtra(EXTRA_BIRTHDAYS_LIST);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BirthdayAdapter(personBirthdays);
        recyclerView.setAdapter(adapter);
        //перенос к сегодняшней дате
        Button todayButton = findViewById(R.id.todayButton);
        todayButton.setOnClickListener(v -> scrollToToday(recyclerView));

        // настройка спинера
        Spinner sortSpinner = findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        // слушатель выбора в спиннере
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (personBirthdays != null) {
                    //<item>По дате</item>
                    if (position == 0) {
                        sortAndUpdateList((p1, p2) -> p1.getDate().compareTo(p2.getDate()));
                    //<item>По имени</item>
                    } else if (position == 1) {
                        sortAndUpdateList((p1, p2) -> p1.getName().compareTo(p2.getName()));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //игнорируем
            }
        });

        // Чёрный фон для статус-бара и панели навигации
        getWindow().setStatusBarColor(Color.BLACK);  // Статус-бар
        getWindow().setNavigationBarColor(Color.BLACK);  // Панель навигации
    }

    /**
     * Прокручивает RecyclerView к ghost записи с сегодняшней датой
     */
    private void scrollToToday(RecyclerView recyclerView) {
        if (personBirthdays == null || personBirthdays.isEmpty()) return;

        // Создаём ghost запись
        PersonBirthday todayGhost = new PersonBirthday();
        todayGhost.setName(BirthdayAdapter.TODAY);
        todayGhost.setSurname(EMPTY);
        todayGhost.setDate(LocalDate.now()); // сегодняшняя дата

        // Вставляем в список (1 раз)
        if (personBirthdays.stream().noneMatch(p -> p.getName().equals(todayGhost.getName()))) {
            personBirthdays.add(todayGhost);
        }
        // Сортируем
        personBirthdays.sort(
                Comparator.comparing((PersonBirthday pb) -> pb.getDate().getMonthValue())
                        .thenComparing(pb -> pb.getDate().getDayOfMonth())
        );
        // Находим индекс ghost
        int index = personBirthdays.indexOf(todayGhost);
        // Прокручиваем RecyclerView к TODAY
        recyclerView.scrollToPosition(index);
        adapter.notifyDataSetChanged();
    }

    /**
     * Обновляет список людей и обновляет адаптер с анимацией!!!
     */
    private void sortAndUpdateList(Comparator<PersonBirthday> comparator) {
        List<PersonBirthday> oldList = new ArrayList<>(personBirthdays);

        // Сортируем основной список
        personBirthdays.sort(comparator);

        // Считаем Diff для перетасовки
        //TODO bug в areItemsTheSame: ByDate -> TODAY -> ByName вызывает NPE
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new BirthdayDiffCallback(oldList, personBirthdays)
        );
        //перестраиваем адаптер
        diffResult.dispatchUpdatesTo(adapter);
    }

}
